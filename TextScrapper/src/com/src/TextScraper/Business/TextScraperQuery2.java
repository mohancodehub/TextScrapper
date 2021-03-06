package com.src.TextScraper.Business;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.src.Exceptions.IllegalPageNumberException;
import com.src.TextScraper.Bean.ProductDetails;

/**
 * This class is used to evaluate the query 2
 * which fetches the results based on the user defined
 * search keyword and the user defined page number for 
 * the results to be fetched.
 * @author mohan
 *
 */
public class TextScraperQuery2 extends TextScraperAbstract{
	
	// List of products from as a result of the query2
	private List<ProductDetails> productList;
	
	
	
	public List<ProductDetails> getProductList() {
		return productList;
	}

	public void setProductList(List<ProductDetails> productList) {
		this.productList = productList;
	}

	public TextScraperQuery2(){
		super();
		productList = new ArrayList<ProductDetails>();
	}
	
	/**
	 * This method is the public interface method
	 * to be called by the user and the user won't 
	 * know about the implementation logic
	 */
	public void executeQuery(String strSearchKeyword,Integer pageNo) 
			throws NullPointerException,MalformedURLException, IOException{
		
		//no string object
		if(strSearchKeyword == null){ throw new NullPointerException("[ERROR]: Empty search keyword."); }
		
		// Base exit
		if (strSearchKeyword.length() == 0 || pageNo == null) {
			throw new NullPointerException("[ERROR]: Search Keyword or page number is empty");
		}
		
		// call the super class common method for both queries to process the search keyword
		// pre process - URLEncoding, Build URL for fetching, Retrieve the Number of results for the query
		try {
			super.preProcess(strSearchKeyword,pageNo);
		} catch (IllegalPageNumberException e) {
			System.out.println(e.getMessage());
		}
		
		//Retrieve the List of Products
		this.retrieveContentUtility();
		
		//display the products
		this.displayResult();
	}
	
	/**
	 * This method implements the main algorithm for query2
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void retrieveContentUtility() 
			throws MalformedURLException, IOException{
		
		int idCounter = 1;	//counter for html dynamic ID element for product details retrieval
			
		//loop till the fetched number of results to create a result object
		if(this.getiResultCount() != 0){
			for(;idCounter <= this.getiResultCount(); idCounter++){
				
				//idCounter to dynamic ID tag of HTML content for Product Title	
				Element content = doc.getElementById("quickLookItem-"+idCounter);
				if(content == null){ continue; }	// continue because some pages might not display 40 items
													// as some might display < 40 and some may display > 40
				
				//Product Title
				Element title = content.getElementById("nameQA"+idCounter);		
				if(title == null){ throw new NullPointerException("[ERROR]: Element for Title is retrieved empty."); }
				
				//Merchant Name
				Elements merchant = content.getElementsByClass("newMerchantName");
				if(merchant == null){ throw new NullPointerException("[ERROR]: Element for vendor is retrieved empty."); }
				
				//Shipping Details Free and Rated
				String tempShip = null;
				//Free shipping
				Elements shipping = content.getElementsByClass("freeShip");		
				for(Element e : shipping){
					tempShip = e.text();
					break;
				}
				
				
				// Shipping Price Tax
				Elements shippingPriceTax = content.getElementsByClass("calc");		
				if(shippingPriceTax != null){  
					for(Element e : shippingPriceTax){
						tempShip = e.text().trim();
						tempShip = tempShip.replaceAll("\\+"," ").trim();		// using \\ because + has reg exp meaning
						break;
					}
				}				
				
				//Stores Number
				Element stores = content. getElementById("numStoresQA"+idCounter);
				String tempStore = null;
				if(stores != null){ tempStore = stores.text(); }
								
				//Product Price
				Elements price = content.getElementsByClass("productPrice");
				if(price == null){ throw new NullPointerException("[ERROR]: Element for price is retrieved empty."); }
							
				//Create a Product Object
				ProductDetails objProductDetails = new 
						ProductDetails(
								title.attr("title"),
								price.text(),tempShip,
								merchant.text(),tempStore);
				
				//add to list of products from result
				productList.add(objProductDetails);
			}
		}
	}
	
	/**
	 * This method is used to display the result of the query
	 */
	private void displayResult(){
		if(productList != null && productList.size() != 0){
			System.out.println();
			int i = 1;
			for(ProductDetails pd : productList){
				System.out.println("Item Number: "+ i);
				System.out.println("Item Title: "+ pd.getStrProductName());
				System.out.println("Item Price: "+ pd.getDbPrice());
				System.out.println("Vendor Name: "+ pd.getStrVendor() +" "+ 
													pd.getiStores());
				System.out.println("Shipping: "+ pd.getDbShipPrice());
				i++;
				System.out.println("----------------------------------");
			}
		}else{
			System.out.println("[ERROR]: No result to display");
		}
	}
}

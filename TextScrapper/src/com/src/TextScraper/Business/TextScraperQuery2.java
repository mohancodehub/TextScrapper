package com.src.TextScraper.Business;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
	
	public TextScraperQuery2(){
		super();
		productList = new ArrayList<ProductDetails>();
	}
	
	/**
	 * This method is the TextScrapper Main algorithm for Query 2
	 * @param strSearchKeyword
	 * @param pageNo
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public void executeQuery(String strSearchKeyword,Integer pageNo) 
			throws NullPointerException,MalformedURLException, IOException{
		
		// Base exit
		if(strSearchKeyword == null || strSearchKeyword.length() == 0 || pageNo == null){
			throw new NullPointerException("[ERROR]: Search Keyword or page number is empty");
		}
		
		int idCounter = 1;
				
		// remove beginning and trailing spaces and replace space in between keywords with %20
		String strProcessedCategory = objURLUtilities.preProcessURLKeywords(strSearchKeyword);
		if(strProcessedCategory == null){ throw new NullPointerException("[ERROR]: URL preprocessing failed."); }
		
		// Build the URL
		String strURL = objURLUtilities.buildURL(strProcessedCategory,pageNo);		
		if(strURL == null){ throw new NullPointerException("[ERROR]: URL building failed."); }
		
		//URL used for result fetch
		System.out.println("[INFO]: URL: "+strURL);
		
		// Create the DOM
		doc = objParserUtilities.createDOMDocument(strURL);
		if(doc == null){ throw new NullPointerException("[ERROR]: DOM creation failed."); }
		
		//Total number of results retrieved
		Elements resultsNo = doc.getElementsByClass("numTotalResults");
		if(resultsNo == null){ throw new NullPointerException("[ERROR]: No results fetched."); }
		
		// Set the total results fetched count
		this.setiResultCount(Integer.parseInt(resultsNo.text().toString().split(" ")[3]));
		
		//loop till the fetched number of results to create a result object
		if(this.getiResultCount() != 0){
			for(;idCounter <= this.getiResultCount(); idCounter++){
				
				//idCounter to dynamic ID tag of HTML content for Product Title	
				Element content = doc.getElementById("quickLookItem-"+idCounter);
				if(content == null){ throw new NullPointerException("[ERROR]: Element for Product is retrieved empty."); }
				
				//Product Title
				Element title = content.getElementById("nameQA"+idCounter);		
				if(title == null){ throw new NullPointerException("[ERROR]: Element for Title is retrieved empty."); }
				
				//Merchant Name
				Elements merchant = content.getElementsByClass("newMerchantName");
				if(merchant == null){ throw new NullPointerException("[ERROR]: Element for vendor is retrieved empty."); }
				
				//Shipping Details
				Elements shipping = content.getElementsByClass("freeShip");		
				if(shipping == null){ throw new NullPointerException("[ERROR]: Element for Shipping is retrieved empty."); }
				
				//Stores Number
				Element stores = content. getElementById("numStoresQA"+idCounter);
				String tempStore = null;
				if(stores != null){ tempStore = stores.text(); }
								
				//Product Price
				Elements price = content.getElementsByClass("productPrice");
				if(price == null){ throw new NullPointerException("[ERROR]: Element for price is retrieved empty."); }
				String tempShip = null;
				for(Element e : shipping){
					tempShip = e.text();
					break;
				}
				
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
	public void displayResult(){
		if(productList != null && productList.size() != 0){
			System.out.println();
			System.out.println("TOTAL RESULTS: "+ this.getiResultCount());
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
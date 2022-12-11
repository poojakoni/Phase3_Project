package Phase3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EndtoEnd_Test {
	Response response;
	RequestSpecification request;
	Map<String,Object> MapObj = new HashMap<String,Object>();
	String baseUri = "http://localhost:3000";

	@Test
	public void test1() {     //one test for case using TestNg
		
		//get all employees		
		System.out.println("All the employees");
		response = GetAllEmployee();
		Assert.assertEquals(200, response.statusCode());
		
		//get single employee
		System.out.println("The single employee");
		response = GetSingleEmp(2);
		Assert.assertEquals(200, response.statusCode());
		
		//create new employee
		System.out.println("The new employee is: ");
		response = CreateEmployee("John", "8000");
		JsonPath jpath = response.jsonPath();
		int empId = jpath.get("id");
		Assert.assertEquals(201, response.statusCode());
		
		//validating new created employee using assertEquals					
		response = GetSingleEmp(empId);
		JsonPath jpath2 = response.jsonPath();
		String name = jpath2.get("name");
		System.out.println("The name is: " + name);
		Assert.assertEquals(name, "John");
		Assert.assertEquals(200, response.statusCode());
		
		//updating employee John as Smita
		System.out.println("The updated name is: ");
		response = UpdateEmployee(empId,"Smith", "8000");
		System.out.println(response.getBody().asString());
		Assert.assertEquals(200, response.statusCode());
		
		//Again validating updated employee using assertEquals and name2
		response = GetSingleEmp(empId);
		JsonPath jpath3 = response.jsonPath();
		String names2 = jpath3.get("name");
		System.out.println("The name is: " + names2);
		Assert.assertEquals(names2, "Smith");
		Assert.assertEquals(200, response.statusCode());
		
		//delet employee
		response = DeleteEmployee(empId);
		Assert.assertEquals(200, response.statusCode());

		//after deleting conforming 404 response code
		response = GetSingleEmp(empId);
		Assert.assertEquals(404, response.statusCode());
		
		
		//after deleting getting all employees using contains
		System.out.println("All the employees");
		response = GetAllEmployee();
		JsonPath jpath5 = response.jsonPath();
		List<String> Ids = jpath5.get("id");
		Assert.assertFalse(Ids.contains(String.valueOf(empId)));
	
		
	}
	
			
	public Response GetAllEmployee() {
		
		RestAssured.baseURI = this.baseUri;
		request = RestAssured.given();
		response = request.get("employees");
		System.out.println(response.getBody().asString());
		return response;
	}
	
	public Response GetSingleEmp(int empId) {
		
		RestAssured.baseURI = this.baseUri;
		request = RestAssured.given();
		response =request.get("employees/"+empId); 
		return response;
	}
	
	public Response CreateEmployee(String Name, String Salary) {
		
		RestAssured.baseURI = this.baseUri;
		request = RestAssured.given();
		MapObj.put("name", Name);
    	MapObj.put("salary", Salary);
    	
    	response = request.contentType(ContentType.JSON).accept(ContentType.JSON).body(MapObj).post("employees/create");
    	return response;
	}
	
	public Response UpdateEmployee(int empId, String Name, String Salary) {
		
		RestAssured.baseURI = this.baseUri;
		request = RestAssured.given();
		MapObj.put("id", empId);
		MapObj.put("name", Name);
		MapObj.put("salary", Salary);
    	    	
    	response = request.contentType(ContentType.JSON).accept(ContentType.JSON).body(MapObj).patch("employees/"+empId);
		return response;
	}
	
	public Response DeleteEmployee(int empId) {
		
		RestAssured.baseURI = this.baseUri;
		request = RestAssured.given();
		response = request.delete("employees/"+empId);
		return response;
	}
}
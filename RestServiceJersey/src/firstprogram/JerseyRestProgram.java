package firstprogram;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.FormParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.DefaultValue;

import firstprogram.Student;

@Path("/")
public class JerseyRestProgram {
	   
   @GET
   @Produces("text/plain")
   public String sayPlainTextHello() {
	   return "<h1>HI</h1>";
   }
	
   @GET
   @Produces("text/html")
   public String doGetAsHtml() {
   return "<h1>HI</h1>";
   }
  
   @GET
   @Produces("application/json")
   public Student doGetasJson() {
	   Student stud = new Student();
	   stud.name="Gautham";
	   stud.age= 23;
	   return stud;
}
   
    @GET
	@Path("add/query")
	public Response getUsers(@DefaultValue("2") @QueryParam("a") int a, @DefaultValue("3") @QueryParam("b") int b) {
		
		int result = a+b;
		return Response
		   .status(200)
		   .entity("Adding two numbers " +  a + " and " + b + " is " + result).build();
   }
    
	@GET  
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/{param}")  
    public Response getMessage(@PathParam("param") String msg) {  
        String output = "Genius says : " + msg;  
        return Response.status(200).entity(output).build();  
    }
	
	@POST
	@Consumes("application/x-www-form-urlencoded")
	public String post(@FormParam("fname") String fname , @FormParam("lname") String lname) {
		return "First name is " + fname + "<br> <br>" + "Last name is " + lname ;
	}
	
}

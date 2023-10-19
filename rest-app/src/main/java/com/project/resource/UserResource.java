package com.project.resource;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.project.DbUtil.DatabaseUtil;
import com.project.model.User;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource{
	
	//get all users 
	
	@GET
	public List<User> getAllUsers(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("limit") @DefaultValue("10") int limit){
		List<User> users = new ArrayList<User>();
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection();
			int offset = (page-1)*limit;
			String sql = "SELECT * FROM info LIMIT ? OFFSET ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, limit);
			statement.setInt(2, offset);
			ResultSet resultSet = statement.executeQuery();
			 while (resultSet.next()) {
	                User user = new User();
	                user.setId(resultSet.getInt("id"));
	                user.setFirstName(resultSet.getString("first_name"));
	                user.setLastName(resultSet.getString("last_name"));
	                user.setUsername(resultSet.getString("username"));
	                user.setDepartment(resultSet.getString("department"));
	                user.setPassword(resultSet.getString("password"));
	                users.add(user);
	            }
		} catch (Exception e) {
			// TODO: handle exception
		}
		return users;
	}
	
	//GET- Get user by Id
	@GET
	@Path("/{id}")
	public Response getUserbyId(@PathParam("id") int id) {
		 User user = getUserFromDatabase(id);
		 if (user != null) {
		        return Response.status(Response.Status.OK).entity(user).build();
		    } else {
		        return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
		    }
	
	}

	private User getUserFromDatabase(int id) {
		 Connection connection = null;
		    try {
		        connection = DatabaseUtil.getConnection();
		        String sql = "SELECT * FROM info WHERE id = ?";
		        PreparedStatement statement = connection.prepareStatement(sql);
		        statement.setInt(1, id);
		        ResultSet resultSet = statement.executeQuery();
		        if (resultSet.next()) {
		            User user = new User();
		            user.setId(resultSet.getInt("id"));
		            user.setFirstName(resultSet.getString("first_name"));
		            user.setLastName(resultSet.getString("last_name"));
		            user.setUsername(resultSet.getString("username"));
		            user.setDepartment(resultSet.getString("department"));
		            user.setPassword(resultSet.getString("password"));
		            return user;
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    } 
		    return null;
	}
	//getting users by username
	@GET
	@Path("/by-username")
	public List<User> getUsersByUsername(@QueryParam("username") String username){
		List<User> users = new ArrayList<User>();
		Connection connection  = null;
		try {
			connection = DatabaseUtil.getConnection();
			String sql = "select * from info where username=?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, username);
			ResultSet resultSet = statement.executeQuery();
			while(resultSet.next()) {
				 User user = new User();
	                user.setId(resultSet.getInt("id"));
	                user.setFirstName(resultSet.getString("first_name"));
	                user.setLastName(resultSet.getString("last_name"));
	                user.setUsername(resultSet.getString("username"));
	                user.setDepartment(resultSet.getString("department"));
	                user.setPassword(resultSet.getString("password"));
	                users.add(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}
	//get all users by department
	@GET
	@Path("/by-department")
	public List<User> getUsersByDepartment(@QueryParam("department") String department){
		List<User> users = new ArrayList<User>();
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection();
			String sql = "select * from info where department = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, department);
			ResultSet resultSet = statement.executeQuery();
			while(resultSet.next()) {
				 User user = new User();
	                user.setId(resultSet.getInt("id"));
	                user.setFirstName(resultSet.getString("first_name"));
	                user.setLastName(resultSet.getString("last_name"));
	                user.setUsername(resultSet.getString("username"));
	                user.setDepartment(resultSet.getString("department"));
	                user.setPassword(resultSet.getString("password"));
	                users.add(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}
	
	//posting a user 
	@POST
	public Response createUser(User user) {
		boolean isUsernameDuplicate = checkUsernameDuplicate(user.getUsername());
		if(isUsernameDuplicate) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Duplicacy of username is not allowed").build();
		}
		else{
			int result = insertUserIntoDatabase(user);
			if(result>0) {
				return Response.status(Response.Status.CREATED).entity("User created successfully").build();
				
			}
			else {
				 return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create user").build();
			}
			
		}
	}

	private int insertUserIntoDatabase(User user) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection();
			String sql = "insert into info (first_name, last_name, username, department, password) VALUES (?, ?, ?, ?, ?)";
			  PreparedStatement statement = connection.prepareStatement(sql);
	            statement.setString(1, user.getFirstName());
	            statement.setString(2, user.getLastName());
	            statement.setString(3, user.getUsername());
	            statement.setString(4, user.getDepartment());
	            statement.setString(5, user.getPassword());
	            return statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private boolean checkUsernameDuplicate(String username) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection();
			String sql = "select count(*) from info where username=?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, username);
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			int count = resultSet.getInt(1);
			return count>0;
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//update user by id 
	@PUT
	@Path("/{id}")
	public Response updateUser(@PathParam("id") int id, User user) {
		boolean userExists = checkUserExists(id);
		if(! userExists) {
			 return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
		}
		int result = updateUserInDatabase(id,user);
		if(result>0) {
			 return Response.status(Response.Status.OK).entity("User updated successfully").build();
			 
		}
		else {
			 return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to update user").build();
		}
	}

	private boolean checkUserExists(int id) {
		Connection connection = null;
	    try {
	        connection = DatabaseUtil.getConnection();
	        String sql = "SELECT COUNT(*) FROM info WHERE id = ?";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        statement.setInt(1, id);
	        ResultSet resultSet = statement.executeQuery();
	        resultSet.next();
	        int count = resultSet.getInt(1);
	        return count > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } 
		return false;
	}

	private int updateUserInDatabase(int id, User user) {
		 Connection connection = null;
		    try {
		        connection = DatabaseUtil.getConnection();
		        String sql = "UPDATE info SET first_name = ?, last_name = ?, username = ?, department = ? WHERE id = ?";
		        PreparedStatement statement = connection.prepareStatement(sql);
		        statement.setString(1, user.getFirstName());
		        statement.setString(2, user.getLastName());
		        statement.setString(3, user.getUsername());
		        statement.setString(4, user.getDepartment());
		        statement.setString(5, user.getPassword());
		        statement.setInt(5, id);
		        return statement.executeUpdate();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		return 0;
	}
	
	@DELETE
	@Path("/{id}")
	public Response deleteUser(@PathParam("id") int id) {
		boolean userExists = checkUserExists(id);
		if(!userExists) {
			return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
		}
		int result = deleteUserFromDatabase(id);
		if(result>0) {
			 return Response.status(Response.Status.OK).entity("User deleted successfully").build();
			 
		}
		else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to delete user").build();
			
		}
	}

	private int deleteUserFromDatabase(int id) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection();
			String sql = "delete from info where id=?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			return statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	@Path("{path: .*}")
	@GET
	public Response undefinedPath() {
		 return Response.status(Response.Status.NOT_FOUND).entity("Undefined path").build();
	}
	
}





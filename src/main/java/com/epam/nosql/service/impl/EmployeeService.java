package com.epam.nosql.service.impl;

import com.epam.nosql.model.Response;
import com.epam.nosql.model.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.Request;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService implements com.epam.nosql.service.EmployeeService {

    public static final String METHOD_GET = "GET";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_PUT = "PUT";

    private static final String INDEX = "employees";

    private RestClient esRestClient;
    private ObjectMapper jsonMapper = new ObjectMapper();

    public EmployeeService(@Autowired RestClient esRestClient) {
        this.esRestClient = esRestClient;
    }

    @Override
    public List<Employee> findAll() {
        Request request = new Request(METHOD_GET, "/" + INDEX + "/_search");
        try {
            org.elasticsearch.client.Response esResponse = esRestClient.performRequest(request);
            JsonNode jsonTree = jsonMapper.readTree(EntityUtils.toString(esResponse.getEntity()));

            JsonNode hitNodes = jsonTree.path("hits").path("hits");
            List<Employee> employeeList = new ArrayList<>();

            if (hitNodes.isArray()) {
                for (JsonNode hit : hitNodes) {
                    JsonNode sourceData = hit.get("_source");
                    if (sourceData != null) {
                        Employee employeeObject = jsonMapper.treeToValue(sourceData, Employee.class);
                        employeeObject.setId(hit.get("_id").asText());
                        employeeList.add(employeeObject);
                    }
                }
            }
            return employeeList;
        } catch (IOException ioException) {
            throw new RuntimeException("Failed to fetch employees list", ioException);
        }
    }

    @Override
    public Employee getById(String employeeId) {
        Request request = new Request(METHOD_GET, "/employees/_doc/" + employeeId);
        try {
            org.elasticsearch.client.Response esResponse = esRestClient.performRequest(request);
            JsonNode jsonTree = jsonMapper.readTree(EntityUtils.toString(esResponse.getEntity()));

            JsonNode sourceNode = jsonTree.get("_source");
            if (sourceNode == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
            }
            Employee emp = jsonMapper.treeToValue(sourceNode, Employee.class);
            emp.setId(employeeId);
            return emp;

        } catch (ResponseException ex) {
            handleResponseExceptions(employeeId, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading employee data", ex);
        }
    }

    @Override
    public Response create(String newId, Employee empData) {
        try {
            String serializedEmpObject = jsonMapper.writeValueAsString(empData);
            Request employeeRequest = new Request(METHOD_PUT, "/" + INDEX + "/_doc/" + newId);
            employeeRequest.setJsonEntity(serializedEmpObject);

            org.elasticsearch.client.Response esResponse = esRestClient.performRequest(employeeRequest);
            int statusCode = esResponse.getStatusLine().getStatusCode();

            boolean successResult = statusCode == 200 || statusCode == 201;
            String message = successResult ? "Employee created successfully" : "Failed to create employee";

            return new Response(successResult, message, newId, INDEX);
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to parse employee data", ex);
        } catch (IOException ex) {
            throw new RuntimeException("Error occurred during employee creation", ex);
        }
    }

    @Override
    public Response delete(String empId) {
        Request request = new Request(METHOD_DELETE, "/" + INDEX + "/_doc/" + empId);
        try {
            org.elasticsearch.client.Response esResponse = esRestClient.performRequest(request);
            int statusCode = esResponse.getStatusLine().getStatusCode();

            boolean deletionSuccess = statusCode == 200;
            String responseMessage = deletionSuccess ? "Employee deleted successfully" : "Employee not found";

            return new Response(deletionSuccess, responseMessage, empId, INDEX);

        } catch (ResponseException ex) {
            handleResponseExceptions(empId, ex);
            throw new RuntimeException("Unexpected error while deleting employee.");
        } catch (IOException ex) {
            throw new RuntimeException("Error in deleting employee: " + empId, ex);
        }
    }

    @Override
    public List<Employee> searchByField(String field, String value) {
        String searchQuery = "{ \"query\": { \"match\": { \"" + field + "\": \"" + value + "\" } } }";
        Request queryRequest = new Request(METHOD_GET, "/" + INDEX + "/_search");
        queryRequest.setJsonEntity(searchQuery);

        try {
            org.elasticsearch.client.Response esResponse = esRestClient.performRequest(queryRequest);
            JsonNode jsonTree = jsonMapper.readTree(EntityUtils.toString(esResponse.getEntity()));

            JsonNode hitNodes = jsonTree.path("hits").path("hits");
            List<Employee> employeeList = new ArrayList<>();

            if (hitNodes.isArray()) {
                for (JsonNode hit : hitNodes) {
                    JsonNode sourceData = hit.get("_source");
                    if (sourceData != null) {
                        Employee employee = jsonMapper.treeToValue(sourceData, Employee.class);
                        employee.setId(hit.get("_id").asText());
                        employeeList.add(employee);
                    }
                }
            }
            return employeeList;

        } catch (ResponseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to execute search operation", ex);
        } catch (IOException ex) {
            throw new RuntimeException("Error searching employees for field: " + field, ex);
        }
    }

    @Override
    public JsonNode aggregate(String aggField, String metric, String metricField) {
        String aggregationRequest =
                "{ \"size\": 0, \"aggs\": { \"group_by_" + aggField + "\" : { \"terms\": { \"field\": \"" + aggField + "\" }, "
                        + "\"aggs\": { \"" + metric + "\" : { \"" + metric + "\": { \"field\": \"" + metricField + "\" } } } } } }";

        Request req = new Request(METHOD_GET, "/" + INDEX + "/_search");
        req.setJsonEntity(aggregationRequest);

        try {
            org.elasticsearch.client.Response esResponse = esRestClient.performRequest(req);
            String resultBody = EntityUtils.toString(esResponse.getEntity());

            JsonNode jsonTree = jsonMapper.readTree(resultBody);
            return jsonTree.has("aggregations") ? jsonTree.get("aggregations") : jsonMapper.createObjectNode();
        } catch (ResponseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error performing aggregation", ex);
        } catch (IOException ex) {
            throw new RuntimeException("Error generating aggregation on: " + metricField, ex);
        }
    }

    private void handleResponseExceptions(String resourceId, ResponseException responseEx) {
        if (responseEx.getResponse().getStatusLine().getStatusCode() == 404) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found for ID: " + resourceId);
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Elasticsearch operation failed", responseEx);
    }
}

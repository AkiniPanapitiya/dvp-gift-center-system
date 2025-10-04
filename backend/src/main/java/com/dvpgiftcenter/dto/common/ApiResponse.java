package com.dvpgiftcenter.dto.common;

public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private Object error;
    
    // Constructors
    public ApiResponse() {}
    
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    // Static factory methods
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
    
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>(false, message);
        return response;
    }
    
    public static <T> ApiResponse<T> error(String message, Object error) {
        ApiResponse<T> response = new ApiResponse<>(false, message);
        response.setError(error);
        return response;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    
    public Object getError() { return error; }
    public void setError(Object error) { this.error = error; }
}
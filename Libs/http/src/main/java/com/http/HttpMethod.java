package com.http;

public enum HttpMethod {
	 GET("GET"),
     POST("POST"),
     PUT("PUT"),
     HEAD("HEAD"),
     MOVE("MOVE"),
     COPY("COPY"),
     DELETE("DELETE"),
     OPTIONS("OPTIONS"),
     TRACE("TRACE"),
     CONNECT("CONNECT");

     private final String value;

     HttpMethod(String value) {
         this.value = value;
     }

     @Override
     public String toString() {
         return this.value;
     }
}

package com.timetrove.Project.common.config.auth;

public interface JwtProperties {
    String SECRET = "zVs#q2;jA+8@"
    		+ "*h<(A$qNqBzN"
    		+ "8>xm7UDv0F?b"
    		+ "N.}f6|Hb5TO>"
    		+ "hBWPV=Htu2JJ"
    		+ "CWmiY?oV)4+$";
    int EXPIRATION_TIME =  1800000; //60000 1분 //1800000 30분
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";

	Long REFRESH_EXPIRATION_TIME = 864000L; //60 1분 //864000 10일
}
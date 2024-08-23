/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.indra.clienteSicov;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
/**
 *
 * @author user
 */
public class Encript {

    private static final String USER_AGENT = "Mozilla/5.0";

    public static String encript(String cadena, String urlServicio)
    {
        System.out.println("------------------------------------------- cadena : " + cadena);
        System.out.println("---------------------encript--------------------------------");
        System.out.println("---------------------------------------- urlServicio : " + cadena);
        System.out.println("ENTRE AL ENCRIPT");
        
        try {
            String cadenaEncode = URLEncoder.encode(cadena);

            URL obj = new URL(urlServicio + cadenaEncode);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar los datos " + e.getMessage());
        }
    }
}
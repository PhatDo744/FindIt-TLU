package com.example.findittlu.data.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateSerializer implements JsonSerializer<Date>, JsonDeserializer<Date> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    
    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(dateFormat.format(src));
    }
    
    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String dateString = json.getAsString();
        
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        
        try {
            // Parse date từ server (format: yyyy-MM-dd)
            Date parsedDate = dateFormat.parse(dateString);
            
            // Debug log
            android.util.Log.d("DateSerializer", "Parsing date from server: " + dateString);
            android.util.Log.d("DateSerializer", "Parsed date: " + parsedDate);
            
            // Đảm bảo ngày không bị ảnh hưởng bởi timezone
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            cal.setTime(parsedDate);
            cal.set(Calendar.HOUR_OF_DAY, 12); // Set giờ về 12:00
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            
            Date finalDate = cal.getTime();
            android.util.Log.d("DateSerializer", "Final date: " + finalDate);
            
            return finalDate;
            
        } catch (ParseException e) {
            android.util.Log.e("DateSerializer", "Error parsing date: " + dateString, e);
            throw new JsonParseException("Error parsing date: " + dateString, e);
        }
    }
} 
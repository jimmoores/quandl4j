package com.jimmoores.quandl.caching;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jimmoores.quandl.TabularResult;
import com.jimmoores.quandl.util.QuandlRuntimeException;

/**
 * CacheManager is responsible for loading and saving of cached Quandl data.
 *
 * Directories are automatically created.
 *
 * It uses gson to serialize and deserialize the data. Just in case we ever have to deal with data of type Calendar,
 * a GMTDateTypeAdapter is registered on the Gson object. Generic conversion routines between JSON data and objects are
 * provided.
 */
public class CacheManager {
  private static final Gson GSON = new GsonBuilder().
    serializeSpecialFloatingPointValues().serializeNulls().create();

  private static Logger s_logger = LoggerFactory.getLogger(CacheManager.class);

  private static final String ENCODING = "UTF-8";

  private String _baseDir;

  /**
   * Creates a CacheManager that will store the data in the directory 'cacheDir'.
   * @param cacheDir the directory for storing data
   */
  public CacheManager(final String cacheDir) {
    if (cacheDir.endsWith("/")) {
      _baseDir = cacheDir;
    } else {
      _baseDir = cacheDir + "/";
    }
  }

  /**
   * Store the data with contents 'tabularResponse_' in a file with name based upon 'quandlCode_'
   * Note that a quandlCode may contain slashes in its name. This may cause deep sub-directory structures.
   * @param quandlCode  The quandl code corresponding to the data
   * @param tabularResponse  the data that will be saved
   */
  public final void store(final String quandlCode, final TabularResult tabularResponse) {
    // the name may contain slashes, we should check if we need to create a new directory
    File file = new File(_baseDir + quandlCode);
    File dir =  file.getParentFile();
    if (!dir.exists()) {
      dir.mkdirs();
    }
    String result = convertToJson(tabularResponse);
    try {
      PrintWriter writer = new PrintWriter(file, ENCODING);
      writer.println(result);
      writer.close();
    } catch (FileNotFoundException fnfe) {
      throw new QuandlRuntimeException("Problem storing " + quandlCode + " in cache,  can't write to file " + file, fnfe);
    } catch (UnsupportedEncodingException ex) {
      throw new QuandlRuntimeException("Encoding not supported.  This should not happen.", ex);
    }
  }

  /** 
   * This method deserializes the specified Json into an object of the specified class.
   * @param <T>  the type of the object to convert to
   * @param toConvert  the string to convert into an object
   * @param clazz  the class of the object to convert to
   * @return the converted object
   */
  public static <T> T convertFromJson(final String toConvert, final Class<T> clazz) {
    return GSON.fromJson(toConvert, clazz);
  }

  /** 
   * This method deserializes the specified Json into an object of the specified class.
   * @param <T>  the type to convert
   * @param toConvert  the string to convert
   * @param typeOfT  the target type to convert into
   * @return the converted object
   */
  public static <T> T convertFromJson(final String toConvert, final Type typeOfT) {
    return GSON.fromJson(toConvert, typeOfT);
  }

  /**
   * This method serializes the specified object into its equivalent Json representation.
   * @param toConvert  the object to convert to a string
   * @return the JSON string representation of the provided object
   */
  public static String convertToJson(final Object toConvert) {
    return GSON.toJson(toConvert);
  }

  /**
   * Load data from the cache.
   * @param quandlCode  The quandl code corresponding to the data
   * @param policy  The cache retention policy
   * @return the TabularResult for this quandlCode, null if the data is too old or missing or if the policy is not set.
   */
  public final TabularResult load(final String quandlCode, final RetentionPolicy policy) {
    if (policy == null) {
      return null;
    }
    File file = new File(_baseDir + quandlCode);
    Path filePath = file.toPath();
    BasicFileAttributes attributes = null;
    try {
      attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
      Calendar creationTime = Calendar.getInstance();
      creationTime.setTimeInMillis(attributes.creationTime().toMillis());
      if (policy.shouldReload(creationTime)) {
        return null;
      }
      List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
      if (lines.size() > 0) {
        String jsonEncoded = lines.get(0);
        TabularResult result = convertFromJson(jsonEncoded, TabularResult.class);
        s_logger.info("Loaded " + quandlCode + " from cache");
        return result;
      }
    } catch (IOException exception) {
      return null;
    }
    return null;
  }

}


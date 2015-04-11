package com.jimmoores.quandl.caching;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jimmoores.quandl.TabularResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Calendar;
import java.util.List;

/**
 * CacheManager is responsible for loading and saving of cached quandl data.
 *
 * Directories are automatically created.
 *
 * It uses gson to serialize and deserialize the data. Just in case we ever have to deal with data of type Calendar,
 * a GMTDateTypeAdapter is registered on the Gson object. Generic conversion routines between json data and objects are
 * provided.
 */
public class CacheManager
{
  private static final Gson gson = new GsonBuilder().
    //registerTypeAdapterFactory(GMTDateTypeAdapter.FACTORY).
    serializeSpecialFloatingPointValues().serializeNulls().create();

  private static Logger s_logger = LoggerFactory.getLogger(CacheManager.class);

  private static final String _encoding = "UTF-8";

  private String  _baseDir;

  /**
   *
   * @param cacheDir_ the directory for storing data
   * creates a CacheManager that will store the data in the directory 'cacheDir_'.
   */
  public CacheManager(String cacheDir_)
  {
    if (cacheDir_.endsWith("/"))
    {
      _baseDir = cacheDir_;
    } else
    {
      _baseDir = cacheDir_ + "/";
    }
  }

  /**
   *
   * @param quandlCode_ The quandl code corresponding to the data
   * @param tabularResponse_ the data that will be saved
   * @throws FileNotFoundException
   * @throws UnsupportedEncodingException
   * Store the data with contents 'tabularResponse_' in a file with name based upon 'quandlCode_'
   * Note that a quandlCode may contain slashes in its name. This may cause deep subdirectory structures
   */
  public void store(String quandlCode_, TabularResult tabularResponse_) throws FileNotFoundException, UnsupportedEncodingException
  {
    // the name may contain slashes, we should check if we need to create a new directory
    File file = new File(_baseDir + quandlCode_);
    File dir =  file.getParentFile() ;
    if (!dir.exists())
    {
      dir.mkdirs();
    }
    String result = convertToJson(tabularResponse_);
    PrintWriter writer = new PrintWriter(file, _encoding);
    writer.println(result);
    writer.close();
  }

  /** This method deserializes the specified Json into an object of the specified class.
   *
   */
  public static  <T> T convertFromJson(String toConvert,  Class<T>  clazz){
    return gson.fromJson(toConvert, clazz);
  }

  /** This method deserializes the specified Json into an object of the specified class.
   *
   */
  public static  <T> T convertFromJson(String toConvert,  Type typeOfT){
    return gson.fromJson(toConvert, typeOfT);
  }

  /**
   * This method serializes the specified object into its equivalent Json representation.
   */
  public static String convertToJson(Object toConvert){
    return gson.toJson(toConvert);

  }

  /**
   *
   * @param quandlCode_ The quandl code corresponding to the data
   * @return the TabularResult for this quandlCode_, null if the data is too old or missing.
   */
  public TabularResult load(String quandlCode_, RetentionPolicy policy_)
  {
    if (policy_ == null)
    {
      return null;
    }
    File file = new File(_baseDir + quandlCode_);
    Path filePath = file.toPath();
    BasicFileAttributes attributes = null;
    try
    {
      attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
      Calendar creationTime = Calendar.getInstance();
      creationTime.setTimeInMillis(attributes.creationTime().toMillis());
      if (policy_.shouldReload(creationTime))
      {
        return null;
      }
      List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
      if (lines.size() > 0)
      {
        String jsonEncoded = lines.get(0);
        TabularResult result = convertFromJson(jsonEncoded, TabularResult.class);
        s_logger.info("Loaded " + quandlCode_ + " from cache");
        return result;
      }
    }
    catch (IOException exception)
    {
      return null;
    }
    return null;
  }

}


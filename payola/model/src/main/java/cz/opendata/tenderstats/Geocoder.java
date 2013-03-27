package cz.opendata.tenderstats;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Thread-safe geocoding of addresses to geographic coordinates.<br>
 * 
 * @see #loadCache(String)
 * @see #saveCache(String)
 * 
 * @version 1.1.2
 * @author Matej Snoha
 */
public class Geocoder implements Serializable {

	private static final long serialVersionUID = -5340672129290535871L;

    /**
	 * Cached geocoding results.
	 */
	private static Map<String, Position> cache = new ConcurrentHashMap<>(10240);

	/**
	 * Loads cache from file.
	 * 
	 * @param filename
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void loadCache(String filename) {
		String backup = filename + ".bak";
		synchronized (cache) {
			try (ObjectInputStream stream = new ObjectInputStream(new GZIPInputStream(new FileInputStream(filename)))) {
				Object obj = stream.readObject();
				cache = new ConcurrentHashMap<>((Map<String, Position>) obj);
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("[WARN] Geocoder cache could not be read from file " + filename + ", trying older version.");
				try (ObjectInputStream stream = new ObjectInputStream(new GZIPInputStream(new FileInputStream(backup)))) {
					Object obj = stream.readObject();
					cache = (Map<String, Position>) obj;
				} catch (IOException | ClassNotFoundException e2) {
					System.out.println("[WARN] Geocoder cache could not be read from file " + backup);
				}
			}
		}
	}

	/**
	 * Loads cache from file if cache is currently empty
	 * 
	 * @param filename
	 */
	public static void loadCacheIfEmpty(String filename) {
		synchronized (cache) {
			if (cache.size() == 0) {
				loadCache(filename);
			}
		}
	}

	/**
	 * Saves cache to file.
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public static void saveCache(String filename) {
		String temp = filename + ".tmp";
		String backup = filename + ".bak";
		synchronized (cache) {
			try {
				if (!Files.exists(Paths.get(temp))) {
					Files.createFile(Paths.get(temp));
				}
			} catch (IOException e) {
			}
			try (ObjectOutputStream stream = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(temp)))) {
				stream.writeObject(cache);
				stream.close();
				if (!Files.exists(Paths.get(filename))) {
					Files.createFile(Paths.get(filename));
				}
				Files.move(Paths.get(filename), Paths.get(backup), StandardCopyOption.REPLACE_EXISTING);
				Files.move(Paths.get(temp), Paths.get(filename), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				System.out.println("[WARN] Geocoder cache could not be saved to file " + filename);
			}
		}
	}

	/**
	 * Clears all cached data.
	 */
	public static void clearCache() {
		synchronized (cache) {
			cache.clear();
		}
	}

	/**
	 * Provider of geocoding services.
	 * 
	 * @author Matej Snoha
	 */
	public interface GeoProvider {

		/**
		 * Converts address to its (approximate) location.
		 * 
		 * @param address
		 * @return Resulting Position object. Returns null when lookup failed.
		 */
		public Position locate(String address);

	}

	/**
	 * Factory for creating GeoProviders from online HTTP services with supported output formats.
	 * 
	 * @author Matej Snoha
	 */
	public static class GeoProviderFactory {

		/**
		 * Constructs a Provider with XML output from given arguments
		 * 
		 * @param requestURL
		 *            URL of service, query string will be appended at the end.
		 * @param latitudeXPath
		 *            XPath of latitude in response from web service.
		 * @param longitudeXPath
		 *            XPath of longitude in response from web service.
		 * @param requestsPerSecond
		 *            Maximum number of requests per second. Set to 0 to disable rate limiting.
		 */
		public static GeoProvider createXMLGeoProvider(String requestURL, String latitudeXPath, String longitudeXPath,
				int requestsPerSecond) {
			return new GeoProviderFactory().new XMLGeoProvider(requestURL, latitudeXPath, longitudeXPath, requestsPerSecond);
		}

		/**
		 * Google Maps GeoProvider<br>
		 * https://developers.google.com/maps/documentation/geocoding/<br>
		 * Limit 2500 requests / ip / day
		 */
		public static final GeoProvider GOOGLE_MAPS =
				createXMLGeoProvider("http://maps.google.com/maps/api/geocode/xml?sensor=false&address=",
						"/GeocodeResponse/result[1]/geometry/location/lat",
						"/GeocodeResponse/result[1]/geometry/location/lng",
						10);

		/**
		 * OpenStreetMap Nominatim GeoProvider<br>
		 * https://wiki.openstreetmap.org/wiki/Nominatim<br>
		 * Bulk limit : 1 / second
		 */
		public static final GeoProvider NOMINATIM =
				createXMLGeoProvider("http://nominatim.openstreetmap.org/search?format=xml&limit=1&q=",
						"/searchresults/place[1]/@lat",
						"/searchresults/place[1]/@lon",
						1);

		/**
		 * Local Gisgraphy instance with data from GeoNames and OpenStreetMap.<br>
		 * No enforced limits.
		 */
		public static final GeoProvider LOCAL =
				createXMLGeoProvider("http://localhost:5555/fulltext/fulltextsearch?allwordsrequired=false&from=1&to=1&q=",
						"/response/result/doc[1]/double[@name=\"lat\"]",
						"/response/result/doc[1]/double[@name=\"lng\"]",
						0);

		/**
		 * Local Gisgraphy instance with data from GeoNames and OpenStreetMap.<br>
		 * No enforced limits.<br>
		 * City-level precision, avoid using streets and countries if possible.
		 */
		public static final GeoProvider LOCAL_CITY =
				createXMLGeoProvider("http://localhost:5555/fulltext/fulltextsearch?allwordsrequired=false&placetype=city"
						+ "&from=1&to=1&q=",
						"/response/result/doc[1]/double[@name=\"lat\"]",
						"/response/result/doc[1]/double[@name=\"lng\"]",
						0);

		/**
		 * Default GeoProvider
		 */
		public static final GeoProvider DEFAULT = LOCAL;

		/**
		 * GeoProvider with XML output.
		 * 
		 * @author Matej Snoha
		 */
		private class XMLGeoProvider implements GeoProvider {

			/**
			 * URL of service, query string will be appended at the end.
			 */
			private String requestURL;

			/**
			 * XPath of latitude in response from web service.
			 */
			private String latitudeXPath;

			/**
			 * XPath of longitude in response from web service.
			 */
			private String longitudeXPath;

			/**
			 * Semaphore used for rate limiting.
			 */
			private TimedSemaphore semaphore = null;

			/**
			 * Constructs a Provider from given arguments. Don't call this constructor directly, use GeoProviderFactory instead.
			 * 
			 * @param requestURL
			 *            URL of service, query string will be appended at the end.
			 * @param latitudeXPath
			 *            XPath of latitude in response from web service.
			 * @param longitudeXPath
			 *            XPath of longitude in response from web service.
			 * @param requestsPerSecond
			 *            Maximum number of requests per second. Set to 0 to disable rate limiting.
			 * @see GeoProviderFactory
			 */
			private XMLGeoProvider(String requestURL, String latitudeXPath, String longitudeXPath, int requestsPerSecond) {
				this.requestURL = requestURL;
				this.latitudeXPath = latitudeXPath;
				this.longitudeXPath = longitudeXPath;
				if (requestsPerSecond > 0) {
					semaphore = new TimedSemaphore(1, TimeUnit.SECONDS, requestsPerSecond);
				}
			}


            /**
			 * Converts address to its (approximate) location.
			 * 
			 * @param address
			 * @return Resulting Position object. Returns null when lookup failed.
			 */
			@Override
			public Position locate(String address) {
				Position pos = new Position();

				try {
					if (semaphore != null) {
						semaphore.acquire();
					}
					URL url = new URL(requestURL + URLEncoder.encode(address, "UTF-8"));
					Document geocoderResultDocument = null;
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					InputSource geocoderResultInputSource = new InputSource(conn.getInputStream());
					geocoderResultDocument =
							DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(geocoderResultInputSource);
					XPath xpath = XPathFactory.newInstance().newXPath();
					NodeList resultNodeList;
					resultNodeList = (NodeList) xpath.evaluate(latitudeXPath, geocoderResultDocument, XPathConstants.NODESET);
					pos.setLatitude(Double.parseDouble(resultNodeList.item(0).getTextContent()));
					resultNodeList = (NodeList) xpath.evaluate(longitudeXPath, geocoderResultDocument, XPathConstants.NODESET);
					pos.setLongitude(Double.parseDouble(resultNodeList.item(0).getTextContent()));
					conn.disconnect();
				} catch (Exception unused) {
				}
				return pos;
			}
		}
	}

	/**
	 * Converts address to its (approximate) location using default provider.
	 * 
	 * @param address
	 * @return Resulting Position object. Returns null when lookup failed.
	 */
	public static Position locate(String address) {
		return locate(address, GeoProviderFactory.DEFAULT, true);
	}

	/**
	 * Converts address to its (approximate) location using specified provider.
	 * 
	 * @param address
	 * @param provider
	 *            Specified Geocoding provider.
	 * @return Resulting Position object. Returns null when lookup failed.
	 * 
	 */
	public static Position locate(String address, GeoProvider provider) {
		return locate(address, provider, true);
	}

	/**
	 * Converts address to its (approximate) location using specified provider.
	 * 
	 * @param address
	 * @param provider
	 *            Specified Geocoding provider.
	 * @param useCache
	 *            Whether to use cached results. If set to false, cached values will be ignored. Upon successful lookup, cached
	 *            position will be overwritten with new.
	 * @return Resulting Position object. Returns null when lookup failed.
	 * 
	 */
	public static Position locate(String address, GeoProvider provider, boolean useCache) {
		if (useCache && cache.containsKey(address)) {
			return cache.get(address);
		} else {
			Position pos = provider.locate(address);
			if (pos != null && !pos.isUndefined()) {
				cache.put(address, pos);
				// System.out.println("[GEOCODER] Cache miss: " + address + " @ " + pos);
				return pos;
			} else {
				return null;
			}
		}
	}

	/**
	 * Converts all adresses in in input set to their position using specified geocoding provider.<br>
	 * 
	 * @param addresses
	 *            List of addresses to process
	 * @param provider
	 *            Geocoding Provider
	 * @param useCache
	 *            Whether to use cached results. If set to false, cached values will be ignored. Upon successful lookup, cached
	 *            position will be overwriten with new.
	 * @param maxConsecutiveFailures
	 *            If 'maxConsecutiveFailures' consecutive lookups fail, operation will be cancelled and the intermediate results
	 *            will be returned.
	 * @return Resulting address --> position Map. Contains no entries for failed lookups.
	 */
	public static Map<String, Position> locate(List<String> addresses, GeoProvider provider, boolean useCache,
			int maxConsecutiveFailures) {
		Map<String, Position> results = new HashMap<>((int) (addresses.size() / 0.7));
		int failed = 0;
		for (String address : addresses) {
			Position position = locate(address, provider, useCache);
			if (position != null) {
				results.put(address, position);
				failed = 0;
			} else {
				failed++;
				if (failed == maxConsecutiveFailures) {
					System.out.println("[ERROR] Aborting bulk processing, too many failures");
					break;
				}
			}
		}
		return results;
	}
}

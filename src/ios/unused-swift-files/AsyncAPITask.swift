/**
 * Helpers for workign with API
 *
 * @copyright Center for Health Enhancement Systems Studies
*/
import Foundation

class AsyncAPITask {
	/**
	 * Execute a URLRequest object
	 *
	 * @param request		Request to execute
	 * @param callback		Callback to execute after we receive our response
	 *
	 * @return URLSessionDataTask
	 */
	static func executeURLRequest(
			request: URLRequest,
			callback: @escaping  ((HTTPURLResponse, NSDictionary?) -> Void)
	) -> URLSessionDataTask {
		let task = URLSession.shared.dataTask(with: request) {
			data, response, error in

			if (response == nil) {
				print("-- Response (nil) --");
				return;
			}

			let http_response = response as! HTTPURLResponse;
			let response_body = String(data: data!, encoding: String.Encoding.utf8) ?? "";

			print("-- Response (" + String(http_response.statusCode) + ") --");
			print(response_body + "\n");

			do {
				let json = try JSONSerialization.jsonObject(with: data!, options: .allowFragments);
				callback(http_response, json as? NSDictionary);

			} catch let error as NSError {
				print("Error with JSON: \(error)\n")
				callback(http_response, nil);
			}
		}

		task.resume();
		return task;
	}

	/**
	 * GET to a url
	 *
	 * @param url			URL to make request to
	 * @param data		Data encoded as a string of the form key1=val1&key2=val2&...
	 * @param callback		Callback to execute
	 *
	 * @return URLSessionDataTask
	 */
	static func GET(url: String, data: String?, callback: @escaping ((HTTPURLResponse, NSDictionary?) -> Void)) -> URLSessionDataTask {
		print("-- Request (GET) --");
		print(url);
		if (data != nil) {
			print(data! + "\n");
		}

		let url_object: URL = URL(string: url)!;
		var request = URLRequest(url: url_object);

		request.httpMethod = "GET";
		request.cachePolicy = NSURLRequest.CachePolicy.reloadIgnoringCacheData;

		if (data != nil) {
			request.httpBody = data?.data(using: String.Encoding.utf8);
		}

		return executeURLRequest(request:request, callback:callback);
	}

	/**
	 * POST to a url
	 *
	 * @param url			URL to make request to
	 * @param data		Data encoded as a string of the form key1=val1&key2=val2&...
	 * @param callback		Callback to execute
	 *
	 * @return URLSessionDataTask
	 */
	static func POST(url: String, data: String?, callback: @escaping ((HTTPURLResponse, NSDictionary?) -> Void)) -> URLSessionDataTask {
		print("-- Request (POST) --");
		print(url);
		if (data != nil) {
			print(data! + "\n");
		}

		let url_object: URL = URL(string: url)!;
		var request = URLRequest(url: url_object);

		request.httpMethod = "POST";
		request.cachePolicy = NSURLRequest.CachePolicy.reloadIgnoringCacheData;

		if (data != nil) {
			request.httpBody = data?.data(using: String.Encoding.utf8);
		}

		return executeURLRequest(request:request, callback:callback);
	}

	/**
	 * POST JSON to a url
	 *
	 * @param url			URL to make request to
	 * @param data		Data to pass
	 * @param callback		Callback to execute
	 *
	 * @return URLSessionDataTask
	 */
	static func POSTJSON(url: String, data: Dictionary<String, Any>, callback: @escaping ((HTTPURLResponse, NSDictionary?) -> Void)) -> URLSessionDataTask {
		let json_data:Data = try! JSONSerialization.data(withJSONObject: data, options: JSONSerialization.WritingOptions.prettyPrinted);

		return POSTJSON(url: url, data: json_data, callback: callback);
	}

	/**
	 * POST JSON to a url
	 *
	 * @param url			URL to make request to
	 * @param data		JSON-encoded data
	 * @param callback		Callback to execute
	 *
	 * @return URLSessionDataTask
	 */
	static func POSTJSON(url: String, data: Data?, callback: @escaping ((HTTPURLResponse, NSDictionary?) -> Void)) -> URLSessionDataTask {
		print("-- Request (POST) --");
		print(url);
		if (data != nil) {
			print(String(data: data!, encoding: .utf8)! + "\n");
		}

		let url_object: URL = URL(string: url)!;
		var request = URLRequest(url: url_object);

		request.httpMethod = "POST";
		request.cachePolicy = NSURLRequest.CachePolicy.reloadIgnoringCacheData;

		if (data != nil) {
			request.httpBody = data;
		}

		request.addValue("application/json", forHTTPHeaderField: "Content-Type");
		request.addValue("application/json", forHTTPHeaderField: "Accept");

		return executeURLRequest(request: request, callback: callback);
	}
}

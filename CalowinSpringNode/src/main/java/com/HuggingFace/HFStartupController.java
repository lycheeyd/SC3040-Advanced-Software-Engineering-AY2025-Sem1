package com.HuggingFace;

import com.controller.HttpReqController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HFStartupController extends HttpReqController {

    public HFStartupController(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Calowin SpringNode Service</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
            overflow: hidden;
        }

        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px;
            text-align: center;
        }

        .header h1 {
            font-size: 2.5rem;
            margin-bottom: 10px;
            font-weight: 300;
        }

        .header p {
            font-size: 1.2rem;
            opacity: 0.9;
        }

        .content {
            padding: 40px;
        }

        .api-section {
            margin-bottom: 40px;
            padding: 30px;
            background: #f8f9fa;
            border-radius: 15px;
            border-left: 5px solid #667eea;
        }

        .api-section h2 {
            color: #333;
            margin-bottom: 20px;
            font-size: 1.5rem;
        }

        .endpoint {
            background: white;
            padding: 20px;
            margin: 15px 0;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            border: 1px solid #e9ecef;
        }

        .endpoint-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }

        .method {
            background: #28a745;
            color: white;
            padding: 5px 15px;
            border-radius: 20px;
            font-size: 0.9rem;
            font-weight: bold;
        }

        .url {
            font-family: 'Courier New', monospace;
            background: #f8f9fa;
            padding: 8px 12px;
            border-radius: 5px;
            color: #495057;
            font-size: 0.9rem;
        }

        .test-btn {
            background: #667eea;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 25px;
            cursor: pointer;
            font-size: 0.9rem;
            transition: all 0.3s ease;
            margin-top: 10px;
        }

        .test-btn:hover {
            background: #5a6fd8;
            transform: translateY(-2px);
        }

        .test-btn:disabled {
            background: #6c757d;
            cursor: not-allowed;
            transform: none;
        }

        .response {
            margin-top: 15px;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 8px;
            border-left: 4px solid #28a745;
            font-family: 'Courier New', monospace;
            font-size: 0.9rem;
            white-space: pre-wrap;
            max-height: 200px;
            overflow-y: auto;
        }

        .error {
            border-left-color: #dc3545;
            background: #f8d7da;
            color: #721c24;
        }

        .loading {
            color: #6c757d;
            font-style: italic;
        }

        .input-group {
            margin: 15px 0;
        }

        .input-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: 500;
            color: #495057;
        }

        .input-group input {
            width: 100%;
            padding: 10px;
            border: 1px solid #ced4da;
            border-radius: 5px;
            font-size: 0.9rem;
        }

        .status-indicator {
            display: inline-block;
            width: 10px;
            height: 10px;
            border-radius: 50%;
            margin-right: 8px;
        }

        .status-up {
            background: #28a745;
        }

        .status-down {
            background: #dc3545;
        }

        .footer {
            text-align: center;
            padding: 20px;
            background: #f8f9fa;
            color: #6c757d;
            font-size: 0.9rem;
        }

        @media (max-width: 768px) {
            .container {
                margin: 10px;
                border-radius: 15px;
            }
            
            .header {
                padding: 30px 20px;
            }
            
            .header h1 {
                font-size: 2rem;
            }
            
            .content {
                padding: 20px;
            }
            
            .endpoint-header {
                flex-direction: column;
                align-items: flex-start;
                gap: 10px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🚀 Calowin Backend API</h1>
            <p>Interactive API Testing Interface</p>
        </div>

        <div class="content">
            <!-- Health Check Section -->
            <div class="api-section">
                <h2>🏥 Health Check</h2>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method">GET</span>
                        <span class="url">/health</span>
                    </div>
                    <p>Check the health status of the Calowin Backend API</p>
                    <button class="test-btn" onclick="testHealth()">Test Health Check</button>
                    <div id="health-response" class="response" style="display: none;"></div>
                </div>
            </div>

            <!-- Home API Section -->
            <div class="api-section">
                <h2>🏠 Home API</h2>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method">GET</span>
                        <span class="url">/</span>
                    </div>
                    <p>Get basic information about the Calowin application</p>
                    <button class="test-btn" onclick="testHome()">Test Home API</button>
                    <div id="home-response" class="response" style="display: none;"></div>
                </div>
            </div>

            <!-- Idk what Section -->
            <div class="api-section">
                <h2>🏥 Click for fun</h2>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method">GET</span>
                        <span class="url">/health</span>
                    </div>
                    <p>Check the health status meowmeow of the Calowin Backend API</p>
                    <button class="test-btn" onclick="testHealth()">Test Health Check</button>
                    <div id="health-response" class="response" style="display: none;"></div>
                </div>
            </div>

            <!-- Trip Methods Section -->
            <div class="api-section">
                <h2>🚶 Trip Methods</h2>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method">GET</span>
                        <span class="url">/central/trips/methods</span>
                    </div>
                    <p>Get available travel methods for trip planning</p>
                    <button class="test-btn" onclick="testTripMethods()">Test Trip Methods</button>
                    <div id="trip-methods-response" class="response" style="display: none;"></div>
                </div>
            </div>

            <!-- Wellness Parks Section -->
            <div class="api-section">
                <h2>🌳 Wellness Parks</h2>
                <div class="endpoint">
                    <div class="endpoint-header">
                        <span class="method">GET</span>
                        <span class="url">/central/wellness/parks</span>
                    </div>
                    <p>Find nearby wellness parks and recreational areas</p>
                    <div class="input-group">
                        <label for="latitude">Latitude:</label>
                        <input type="number" id="latitude" value="1.3521" step="0.0001" placeholder="Enter latitude">
                    </div>
                    <div class="input-group">
                        <label for="longitude">Longitude:</label>
                        <input type="number" id="longitude" value="103.8198" step="0.0001" placeholder="Enter longitude">
                    </div>
                    <button class="test-btn" onclick="testWellnessParks()">Test Wellness Parks</button>
                    <div id="wellness-parks-response" class="response" style="display: none;"></div>
                </div>
            </div>
        </div>

        <div class="footer">
            <p>Calowin Backend API Demo - Built with Spring Boot</p>
        </div>
    </div>

    <script>
        async function makeRequest(url, method = 'GET') {
            try {
                const response = await fetch(url, {
                    method: method,
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    }
                });
                
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                }
                
                const data = await response.json();
                return { success: true, data: data };
            } catch (error) {
                return { success: false, error: error.message };
            }
        }

        function showResponse(elementId, result) {
            const element = document.getElementById(elementId);
            element.style.display = 'block';
            
            if (result.success) {
                element.className = 'response';
                element.textContent = JSON.stringify(result.data, null, 2);
            } else {
                element.className = 'response error';
                element.textContent = `Error: ${result.error}`;
            }
        }

        async function testHealth() {
            const btn = event.target;
            const originalText = btn.textContent;
            btn.textContent = 'Testing...';
            btn.disabled = true;
            
            const result = await makeRequest('/health');
            showResponse('health-response', result);
            
            btn.textContent = originalText;
            btn.disabled = false;
        }

        async function testHome() {
            const btn = event.target;
            const originalText = btn.textContent;
            btn.textContent = 'Testing...';
            btn.disabled = true;
            
            const result = await makeRequest('/');
            showResponse('home-response', result);
            
            btn.textContent = originalText;
            btn.disabled = false;
        }

        async function testTripMethods() {
            const btn = event.target;
            const originalText = btn.textContent;
            btn.textContent = 'Testing...';
            btn.disabled = true;
            
            const result = await makeRequest('/central/trips/methods');
            showResponse('trip-methods-response', result);
            
            btn.textContent = originalText;
            btn.disabled = false;
        }

        async function testWellnessParks() {
            const btn = event.target;
            const originalText = btn.textContent;
            btn.textContent = 'Testing...';
            btn.disabled = true;
            
            const lat = document.getElementById('latitude').value;
            const lon = document.getElementById('longitude').value;
            
            if (!lat || !lon) {
                showResponse('wellness-parks-response', { 
                    success: false, 
                    error: 'Please enter both latitude and longitude values' 
                });
                btn.textContent = originalText;
                btn.disabled = false;
                return;
            }
            
            const result = await makeRequest(`/central/wellness/parks?lat=${lat}&lon=${lon}`);
            showResponse('wellness-parks-response', result);
            
            btn.textContent = originalText;
            btn.disabled = false;
        }

        // Auto-test health on page load
        window.addEventListener('load', function() {
            setTimeout(testHealth, 1000);
        });
    </script>
</body>
</html>
""";
    }
}

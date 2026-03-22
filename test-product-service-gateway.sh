#!/bin/bash
# Dedicated Product Service through API Gateway tests
# Gateway should be running at http://localhost:7001

set -euo pipefail

GATEWAY_URL="http://localhost:7001"

PASSED=0
FAILED=0

print_header() {
  echo
  echo "==============================================="
  echo "$1"
  echo "==============================================="
}

print_success() {
  printf "\e[32m✓ %s\e[0m\n" "$1"
  PASSED=$((PASSED + 1))
}

print_failure() {
  printf "\e[31m✗ %s (%s)\e[0m\n" "$1" "$2"
  FAILED=$((FAILED + 1))
}

run() {
  local method=$1
  local path=$2
  local data=""
  local desc=""

  if [ $# -eq 3 ]; then
    desc=$3
  elif [ $# -eq 4 ]; then
    data=$3
    desc=$4
  else
    echo "Invalid run() usage; expected 3 or 4 args" >&2
    exit 1
  fi

  print_header "$desc"
  echo "Request: $method $GATEWAY_URL$path"

  local resp
  if [ -n "$data" ]; then
    resp=$(curl -s -w "\n%{http_code}" -X "$method" \
      -H 'Content-Type: application/json' \
      -d "$data" \
      "$GATEWAY_URL$path" 2>&1) || true
  else
    resp=$(curl -s -w "\n%{http_code}" -X "$method" \
      "$GATEWAY_URL$path" 2>&1) || true
  fi

  local code
  code=$(echo "$resp" | tail -n1)
  local body
  body=$(echo "$resp" | sed '$d')

  if [[ "$code" =~ ^(200|201|202|204)$ ]]; then
    print_success "$desc (HTTP $code)"
    echo "$body"
  else
    print_failure "$desc" "HTTP $code"
    echo "$body"
  fi
}

# health check
print_header "Gateway health check"
if ! curl -s -o /dev/null -w "%{http_code}" "$GATEWAY_URL/actuator/health" | grep -q '^200$'; then
  echo "Gateway is unreachable at $GATEWAY_URL" >&2
  exit 1
fi
print_success "Gateway is reachable"

# Product endpoints
print_header "Product Service Endpoints via API Gateway"

run POST "/api/v1/products" '{"productId":"PROD001","name":"Laptop","description":"High-performance laptop","price":999.99,"category":"Electronics","stockQuantity":50,"supplier":"TechCorp","barcode":"123456789","sku":"LAPTOP-001","isActive":true}' "Create product PROD001"

run POST "/api/v1/products" '{"productId":"PROD002","name":"Mouse","description":"Wireless mouse","price":29.99,"category":"Electronics","stockQuantity":100,"supplier":"TechCorp","barcode":"987654321","sku":"MOUSE-001","isActive":true}' "Create product PROD002"

run GET "/api/v1/products" "Get all products"
run GET "/api/v1/products/PROD001" "Get product by id PROD001"
run GET "/api/v1/products/active" "Get active products"
run GET "/api/v1/products/category/Electronics" "Get products by category"
run GET "/api/v1/products/supplier/TechCorp" "Get products by supplier"
run GET "/api/v1/products/search?name=Laptop" "Search products by name"
run GET "/api/v1/products/barcode/123456789" "Get product by barcode"
run GET "/api/v1/products/sku/LAPTOP-001" "Get product by SKU"
run GET "/api/v1/products/low-stock?threshold=50" "Get low stock products"
run GET "/api/v1/products/top-selling" "Get top selling products"
run GET "/api/v1/products/price-range?minPrice=10&maxPrice=2000" "Get products by price range"

run PUT "/api/v1/products/PROD001" '{"name":"Laptop Pro","description":"Updated high-performance laptop","price":1099.99,"category":"Electronics","stockQuantity":45,"supplier":"TechCorp","barcode":"123456789","sku":"LAPTOP-001","isActive":true}' "Update product PROD001"
run PUT "/api/v1/products/PROD001/stock?quantity=75" "Update stock PROD001"
run PUT "/api/v1/products/PROD002/stock/reduce?quantity=5" "Reduce stock PROD002"

run DELETE "/api/v1/products/PROD001" "Delete product PROD001"
run DELETE "/api/v1/products/PROD002" "Delete product PROD002"

print_header "Test Summary"
echo "Passed: $PASSED"
echo "Failed: $FAILED"

if [ "$FAILED" -gt 0 ]; then
  exit 1
fi

Release Notes
=============
# 1.2.0
Changed HTTPS URL because RESTEasy and Apache CXF can't handle redirect to https://www.quandl.com which is the address in the TLS certificate.  Disabled 
integration test from main build because test framework misbehaving.

# 1.1.0 
Add support for HTTPS migration, small change, but required test data regeneration.

# 1.0.0
Handles the deprecation of multisets from the Quandl REST API and provides for (configurable) automatic resilience to 
transient errors.

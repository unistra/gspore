package errors

class SporeError extends Exception{
static errorMessages=[
	'name':'A name for this client is required',
	'base_url':'A base URL to the REST Web Service is required',
	'methods':'One method is required to create the client'
	]
}

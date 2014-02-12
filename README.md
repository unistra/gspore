gspore
======

>Gspore is intended on being used as a .jar which is 
>sufficient to allow java or groovy application that include it to consume SPORE implementing webservices.  

>It allows the consuming app to instanciate 
>spore clients on the basis of api descriptions and to use them.
>Clients are generated with a set of class methods that matches the set of functionalities described in the api description, all of which are indeed callable, and send actual HTTP requests constructed according to the
specification.  

>The raw features of the spore client and it's 
>dynamically generated methods can be customized by the middlewares, 
>which are specific workflow rewriters that are added on client scope,
>but can be enabled in a conditional fashion(e.g add authentication element on client scope).

>The standard use of gspore should be something like

1. Generate a client through api descritpion
2. Create and enable, conditionaly or not, Middlewares
3. Make requests

>To summarize, the workflow processes through the following steps:

1. feed() function is given the api description, and returns either
errors or a Spore.
2. In the second case, the Spore either returns errors or it adds itself one class method for each method registered in the api description Json under the entry "methods".
3. By that point, basic HTTP request can be issued, and Middlewares can be enabled.
4. If Middlewares were enabled, the method calls are intercepted and passed through each middleware, in enablement order, being potentially rewritten in the process.
5. Unless the request was canceled, response structuring elements of the request go through each optional middleware post-processing callback in reverse enablement order.
6. If no middleware prevented it from doing so, the request is actually sent.
7. A response is returned to the client.






SPORE CLIENT 
------------

###sample groovy syntax :

>*//Instanciate Json reader*

>**SporeFeeder feed =new SporeFeeder()**

>*//Create Spore*

>**Spore spore = feed.feed("/pathToMyJson/test.json")**

>*//Enable Middleware, from hard-coded class or by generating a modified at runtime Middleware*

>**spore.enable(spore.Middleware,[  
"processRequest":{localArgs->  
localArgs["spore.headers"]=["k":"v"]},  payload:["entry":["subEntry":'value']]  
])**

>*//same thing, with a boolean returning closure to specify wether or not
the Middleware should be enabled*

>**spore.enableIf(spore.Middleware,[payload:["entry":["subEntry":'value']]){args->
			args['name']=="retrieve_page"
			 }**

>*//call method*

>**spore.methodNameFoundInTheJson([arg1:"test",arg2:2,id:"unid"])**

###sample java syntax : 

>*//Instanciate Json feeder*

>**SporeFeeder feed = new SporeFeeder();**
		
>**Spore spore = feed.feed("/pathToMyJson/test.json");**
		
>*//Instanciate Middleware from hard-coded class*

>**Jazzleware j = new Jazzleware();**

>*//enable Middleware conditionnaly*

>**spore.enableIf(j.getClass(), args);**

>**Map<Object,Object> args0 = new HashMap<Object ,Object>();**

>*//call method*

>**spore.invokeMethod("methodNameFoundInTheJson",args0)**

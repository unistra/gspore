gspore
======

>Gspore is intended on being used as a .jar which is 
>sufficient to allow java or groovy application that include it to consume >SPORE implementing webservices.  

>It allows the consuming app to instanciate 
>spore clients on the basis of api descriptions and to use them. Clients >are generated with a set of class methods that matches the set of >functionalities described in the api description, all of which are indeed >callable, and send actual HTTP requests constructed according to the
>specification.  

>The raw features of the spore client and it's 
>dynamically generated methods can be customized by the middlewares, 
>which are specific workflow rewriters that are added on client scope,
>but can be enabled in a conditional fashion.




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

>**spore.enableIf(spore.Middleware,[payload:["k":"v"]]){
			 spore.name!=null
			 }**

>*//call method*

>**spore.methodNameFoundInTheJson([arg1:"test",arg2:2,id:"unid"])**

###sample java syntax : 

>*//Instanciate Json feeder*

>**SporeFeeder feed = new SporeFeeder();**
		
>**Spore spore = feed.feed("/pathToMyJson/test.json");**
		
git>*//Instanciate Middleware from hard-coded class*

>**Jazzleware j = new Jazzleware();**

>*//enable Middleware conditionnaly*

>**spore.enableIf(j.getClass(), args);**

>**Map<Object,Object> args0 = new HashMap<Object ,Object>();**

>*//call method*

>**spore.invokeMethod("methodNameFoundInTheJson",args0)**

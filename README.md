gspore
======
SPORE CLIENT 

sample groovy syntax :

//Instanciate Json reader
SporeFeeder feed =new SporeFeeder()

//Create Spore
Spore spore = feed.feed("/pathToMyJson/test.json")

//enable Middleware, from hard-coded class or by generating a modified at runtime Middleware
spore.enable(spore.Middleware,["processRequest":{localArgs->localArgs["spore.headers"]=["k":"v"]},payload:["entry":["subEntry":'value']]])

//same thing, with a closure as last parameter, to enable only conditionnaly
spore.enableIf(spore.Middleware,[payload:["k":"v"]]){
			 spore.name!=null
			 }

//call method
spore.methodNameFoundInTheJson([arg1:"test",arg2:2,id:"unid"])

sample java syntax : 

//Instanciate Json feeder
SporeFeeder feed = new SporeFeeder();
		
Object o = feed.feed("/pathToMyJson/test.json");
		
Spore spore = (Spore)o;

//Instanciate Middleware from hard-coded class
Jazzleware j = new Jazzleware();

//enable Middleware conditionnaly
spore.enableIf(j.getClass(), map12,j.methodJizz("condition"));

Map<Object,Object> map = new HashMap<Object ,Object>();

//call method
spore.invokeMethod("methodNameFoundInTheJson",map)

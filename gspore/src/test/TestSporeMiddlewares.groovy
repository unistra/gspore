package test

import org.junit.Test
import groovy.util.GroovyTestCase
import static feed.SporeFeeder.feed
import java.net.UnknownHostException
import java.io.FileNotFoundException
import groovy.json.JsonSlurper
import groovy.json.JsonException
import spore.Spore

class TestSporeMiddlewares  extends GroovyTestCase{

	@Test
	void testSporeMiddlewares(){
		Spore z = new Spore([
			'name':'name',
			'base_url':'base_url',
			'methods':[
				"method1" : [
					"path" : "/target/method1",
					"method":"get"
				]
			],
			'user_agent':'user_agent',
			'authority':'authority',
			'formats':'formats',
			'version':'version',
			'authentication':'authentication'
		])
		z.enable(
			middleware.Middleware,
			[
				processRequest:{args->
					args['spore.headers'] = ["Authorization":64536546]
					return { "blabla" }
				}
			]
			)
		z.enable(
			middleware.Middleware,
			[
				processRequest:{args->
					args['spore.headers'] = ["sweetlovelydeath":"iamwaitingforyourbreath"]
					return { "blabla" }
				}
			]
			)
		//println 
		assertEquals 2,z.middlewares.size()
		
	}
}

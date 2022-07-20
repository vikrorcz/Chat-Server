package com.example.plugins

import io.ktor.server.auth.*
import io.ktor.util.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.util.SimpleJWT
import com.example.util.SimpleJWT.jwtRealm
import com.example.util.SimpleJWT.jwtVerifier
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import io.ktor.server.request.*


fun Application.configureSecurity() {

/*
    authentication {
            jwt {
                val jwtAudience = this@configureSecurity.environment.config.property("jwt.audience").getString()
                realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
                verifier(
                    JWT
                        .require(Algorithm.HMAC256("secret"))
                        .withAudience(jwtAudience)
                        .withIssuer(this@configureSecurity.environment.config.property("jwt.domain").getString())
                        .build()
                )
                validate { credential ->
                    //if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
                }
            }
        }

*/


    /*
    authentication {
        jwt {
            val appConfig = HoconApplicationConfig(ConfigFactory.load())
            val jwtSecret = appConfig.property("jwt.secret").getString()
            val jwtIssuer = appConfig.property("jwt.issuer").getString()
            val jwtAudience = appConfig.property("jwt.audience").getString()
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
    */


    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                jwtVerifier
            )
            validate { credential ->
                UserIdPrincipal(credential.payload.getClaim("email").asString())
            }
        }
    }

}

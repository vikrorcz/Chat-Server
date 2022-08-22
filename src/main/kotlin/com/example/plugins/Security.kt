package com.example.plugins

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.example.util.JWTUtil.jwtRealm
import com.example.util.JWTUtil.jwtVerifier
import io.ktor.server.application.*


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
               // UserIdPrincipal(credential.payload.getClaim("email").asString())
                UserIdPrincipal(credential.payload.getClaim("email").asString())
            }
        }
    }

}

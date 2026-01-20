## Solution
This implementation does not use existing libraries like Spring Security. Instead I chose to roll my own solution for learning purposes.

The securing of the endpoints is achieved by calling a method named `authenticate` in the controller before each action. This method then calls the introspect endpoint as set in the application properties and then checking if this token is active and has the appropriate scopes.

## Possible problems and improvements
1. A request to introspect is made on each api hit, which could cause many issues for both the authorisation server and the resourse server. Caching the token details from introspection could mitigate this.
2. Requests to introspect are made without authentication, since there isn't any, but that would probably not be true in a real world scenario.
3. .well-known/openid-configuration provides all the information needed, it could be used to set up all configuration dyamically instead of having it inside the application properties.

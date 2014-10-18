package actions;

import java.lang.annotation.*;

import play.libs.F.Promise;
import play.mvc.*;
import play.mvc.Http.Context;
import play.mvc.Http.*;

public class CorsComposition {

	@With(CorsAction.class)
	@Target({ ElementType.TYPE, ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Cors {
		String value() default "*";
	}
 
	public static class CorsAction extends Action<Cors> {
 
		@Override
		public Promise<Result> call(Context context) throws Throwable {
			Response response = context.response();
			response.setHeader("Access-Control-Allow-Origin", configuration.value());
 
			if (context.request().method().equals("OPTIONS")) {
				response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
				response.setHeader("Access-Control-Max-Age", "3600");
				response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization, X-Auth-Token");
				response.setHeader("Access-Control-Allow-Credentials", "true");
 
				return delegate.call(context);
			}
 
			response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, X-Auth-Token");
			return delegate.call(context);
		}
	}
}

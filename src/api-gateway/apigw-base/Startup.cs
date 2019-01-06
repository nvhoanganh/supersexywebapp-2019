using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Ocelot.DependencyInjection;
using Ocelot.Middleware;

namespace apigatewaybase
{
	public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        public void ConfigureServices(IServiceCollection services)
        {
			services.AddHealthChecks();
			services.AddCors();
			services.AddOcelot();
		}

        public void Configure(IApplicationBuilder app, IHostingEnvironment env)
        {
			if (env.IsDevelopment())
			{
				app.UseDeveloperExceptionPage();
			}

			app.UseHealthChecks("/healthz");

			app.UseCors(config => config
				.AllowAnyOrigin()
				.AllowAnyHeader()
				.AllowAnyMethod());

			app.UseOcelot().Wait();
		}
    }
}

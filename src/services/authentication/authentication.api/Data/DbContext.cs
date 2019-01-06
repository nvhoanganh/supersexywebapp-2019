using authenticationapi.Data;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;

namespace authenticationapi
{
   public class DockerDbContext : IdentityDbContext<ApplicationUser>
   {
      public DockerDbContext(DbContextOptions<DockerDbContext> options)
         : base(options)
      {
      }
   }
}

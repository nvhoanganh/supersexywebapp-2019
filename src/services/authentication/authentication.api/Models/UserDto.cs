using System.ComponentModel.DataAnnotations;

namespace authenticationapi.Models
{
	public class UserDto
	{
		public string Id { get; set; }
		[Required]
		public string FirstName { get; set; }
		[Required]
		public string LastName { get; set; }
		[Required]
		public string Email { get; set; }
		public string Password { get; set; }
	}
}

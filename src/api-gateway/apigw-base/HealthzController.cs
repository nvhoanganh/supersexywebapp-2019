using Microsoft.AspNetCore.Mvc;
namespace apigwbase
{
	[Route("/Heathz")]
	public class HealthzController : Controller
	{
		public IActionResult Index()
		{
			return Ok();
		}
	}
}

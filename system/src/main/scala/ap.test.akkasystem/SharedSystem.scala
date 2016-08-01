package ap.test.akkasystem

import akka.actor.ActorSystem 
import akka.osgi.ActorSystemActivator

import org.osgi.framework.BundleContext

class SharedSystem extends ActorSystemActivator
{
  def configure(bundleContext: BundleContext, system: ActorSystem)
  {
    registerService(bundleContext, system)
    println("actorSystem registered")
  }
}


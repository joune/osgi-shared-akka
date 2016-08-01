package ap.test.app2

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import org.osgi.framework.BundleContext
import org.osgi.service.component.ComponentFactory
import aQute.bnd.annotation.component._
import java.util.{Hashtable => jHT}

/* 
 * this pattern is slightly trickier than the one in app1
 * but, using ComponentFactory it allows to inject dependencies directly into the actor itself
 */
@Component(immediate=true) class Activator 
{
  var system: ActorSystem = _
  var factory: ComponentFactory = _

  @Reference def bindSystem(sys: ActorSystem): Unit = {
    println("got actorSystem "+sys)
    system = sys
  }

  def create: Actor = factory.newInstance(null).getInstance.asInstanceOf[Actor]

  @Reference(target="(component.factory=ap.test.app2)") def bindCF(cf: ComponentFactory): Unit = {
    println("got factory "+cf)
    factory = cf
  }
  
  @Activate def start(bc: BundleContext): Unit = 
    bc.registerService(
      classOf[ActorRef], 
      system.actorOf(Props(create), "app2"),
      new jHT[String,String] {
      put("name", "app2")
    })
}

case class Greet(ref: ActorRef)

@Component(factory="ap.test.app2")
class App2 extends Actor
{
  case class Bro(ref: ActorRef)

  @Reference(optional=true, dynamic=true, target="(name=app1)", unbind="ubindRef") def bindRef(brother: ActorRef): Unit = {
    self ! Greet(brother)
  }
  def ubindRef(gone: ActorRef) = Unit

  def receive: Receive = {
    case Greet(ref) => ref ! "hello from app2 using service binding" 
    case any: String => 
        println(s"got message $any !")
        if (!any.startsWith("thanks"))
          sender ! "thanks mate, gotcha"
  }
}

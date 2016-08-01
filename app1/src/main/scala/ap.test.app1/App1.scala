package ap.test.app1

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import org.osgi.framework.BundleContext
import aQute.bnd.annotation.component._
import java.util.{Hashtable => jHT}

@Component(immediate=true) class Activator 
{
  var me: Option[ActorRef] = None
  var bro: Option[ActorRef] = None

  @Reference def bindSystem(system: ActorSystem): Unit = {
    println("got actorSystem "+system)
    me = Some(system.actorOf(Props[App1], "app1"))
    doGreet
  }
  
  @Reference(optional=true, dynamic=true, target="(name=app2)", unbind="ubindRef") def bindRef(brother: ActorRef): Unit = {
    bro = Some(brother)
    doGreet
  }
  def ubindRef(gone: ActorRef): Unit = bro = None

  @Activate def start(bc: BundleContext): Unit = 
    bc.registerService(classOf[ActorRef], me.get, new jHT[String,String] {
      put("name", "app1")
    })

  // we need both refs to be available
  def doGreet = for {
    mee <- me
    broo <- bro
  } yield mee ! Greet(broo)
}

case class Greet(ref: ActorRef)

class App1 extends Actor
{
  // send a first hello by looking up brother actor. NOTE: if app1 is started before app2, this message is lost!
  context.actorSelection("../app2") ! "initial hello form app1 (maybe lost!)"

  def receive = {
    case Greet(ref) => ref ! "hello from app1 using service binding" 
    case any: String => 
        println(s"got message $any !")
        if (!any.startsWith("thanks"))
          sender ! "thanks mate, gotcha"
  }
}

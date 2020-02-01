package roflsoft.modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import roflsoft.database.PersonDAO

class CoreModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[PersonDAO].asEagerSingleton()
  }
}

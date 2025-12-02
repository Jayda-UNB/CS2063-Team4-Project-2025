package ca.unb.mobiledev.cookiestepper

import android.app.Application
import ca.unb.mobiledev.cookiestepper.db.AppDatabase
import ca.unb.mobiledev.cookiestepper.repositories.StepRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


//application class to set up global application level dependencies like
//database and repository.
class App: Application() {

    //Defining a scop for coroutines tied to the applicaiton's liefspan
    private val applicationScope = CoroutineScope(SupervisorJob())

    //Initialize the database lazily
    private val database by lazy{
        AppDatabase.getDatabase(this, applicationScope)
    }

    //initialize the repository lazily, injecting both DAOs from the database
    //this repository is the only instance the entire app will use
    val repository by lazy{
        StepRepository(
            stepDao = database.stepDao(),
            userProfileDao = database.userProfileDao()
        )
    }


}

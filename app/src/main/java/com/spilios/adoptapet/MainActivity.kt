package com.spilios.adoptapet



import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.spilios.adoptapet.ui.theme.AdoptAPetTheme
import com.spilios.adoptapet.ui.theme.Pink_light
import com.spilios.adoptapet.ui.theme.Pink_dark
import com.spilios.adoptapet.ui.theme.Purple



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContent {
                AdoptAPetTheme {
                    Navigation()
                }
            }
        }
    }

//////////////////All the Navigation/////////////////////////
@Composable
fun Navigation(){
    val authViewModel = AuthViewModel()
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                navController = navController,
                onLoginSuccess = { navController.navigate(Screen.Home.route) }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("${Screen.Details.route}/{petId}") { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val petId = arguments.getString("petId")?.toIntOrNull() ?: return@composable
            DetailScreen(navController, petId)
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(authViewModel = authViewModel, navController = navController)
        }
    }
}

///////////////Home - Main Screen///////////////////////////
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController, authViewModel: AuthViewModel) {
    val pets by remember { mutableStateOf(PetRepository.pets) }
    val (showLogoutDialog, setShowLogoutDialog) = remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = { setShowLogoutDialog(true) },
                    shape = CircleShape,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Logout")
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.dog_cat),
                contentDescription = "App logo",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(pets.size) { index ->
                    val pet = pets[index]
                    PetCard(
                        petId = pet.id,
                        navController = navController
                    )
                }
            }
        }
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onLogoutConfirmed = {
                authViewModel.logout()
                navController.navigate(Screen.Login.route)
            },
            onDismissRequest = { setShowLogoutDialog(false) }
        )
    }
}

///////////PetCard//////////////////
@Composable
fun PetCard(
    petId: Int,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val pet = PetRepository.pets.find { it.id == petId }
    if (pet == null) {
        Text(text = "No pet found")
        return
    }

    val sex = if (pet.sex) {
        Purple
    } else {
        Pink_dark
    }

    Card(
        modifier = modifier
            .padding(16.dp)
            .height(180.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.clickable {
                navController.navigate("${Screen.Details.route}/$petId")
            }
        ) {
            Image(
                painter = painterResource(pet.imageResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(sex)
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = pet.name,
                    //text = "$petId",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

///////////////// Detail screen composable//////////////////
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter") //xwris afto error
@Composable
fun DetailScreen(navController: NavController, petId: Int) {
    
    val pet = remember {
        PetRepository.pets.find { it.id == petId }
    }
    val sex = if (pet != null) {
        if (pet.sex) {
            "He/Him"
        } else {
            "She/Her"
        }
    } else {
        ""
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.popBackStack()
                },
                shape = CircleShape
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back to Home")
            }
        },
        content = {
            if (pet != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(pet.imageResId),
                        contentDescription = "Pet's Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                    )
                    Column (
                        modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Pink_light)
                                .padding(40.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Row (
                                modifier = Modifier,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Text(
                                    text = "${pet.name} ",
                                    fontSize = (24.sp)
                                )
                                Text(text = sex, style = TextStyle(fontSize = 14.sp))
                            }
                            Text(text = "${pet.age} years old", fontSize = (24.sp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row (
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = pet.breed,
                                color = Color.White,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(bottomEnd = 25.dp, topEnd = 25.dp))
                                    .background(Pink_dark)
                                    .padding(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = pet.description,
                                color = Color.White,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(topStart = 25.dp, bottomStart = 25.dp))
                                    .background(Purple)
                                    .padding(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row (
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = pet.health,
                                color = Color.White,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(bottomEnd = 25.dp, topEnd = 25.dp))
                                    .background(Pink_dark)
                                    .padding(16.dp)
                            )
                        }
                    }

                }
            } else {
                Text(text = "Pet not found")
            }
        }
    )
}
///////////////////////////////////////////////////////////////

////////////////////////Auth function//////////////////////////////////////
class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun login(email: String, password: String, onComplete: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
    }

    fun signUp(email: String, password: String, onComplete: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
    }

    fun logout() {
        auth.signOut()
    }
}
/////////////////////////////////////////////////////////////////////////

///////////////////////////////Login/////////////////////////////////////
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    onLoginSuccess: () -> Unit
) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val showErrorDialog = remember { mutableStateOf(false) }
    val wrongCredentialsError = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.dog_cat),
            contentDescription = "app logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Log-In")
        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") }
        )
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (emailState.value.isEmpty() || passwordState.value.isEmpty()) {
                showErrorDialog.value = true
            } else {
                authViewModel.login(emailState.value, passwordState.value) { success ->
                    if (success) {
                        onLoginSuccess() // navigate to main after successful login
                    } else {
                        wrongCredentialsError.value = true
                    }
                }
            }
        }) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Don't Have an account?")
        Button(onClick = {
            navController.navigate(Screen.SignUp.route) // go to signup page
        }) {
            Text(text = "Sign-Up")
        }
    }
    if (showErrorDialog.value) {
        EmptyFieldsErrorDialog(onDismissRequest = { showErrorDialog.value = false })
    }
    if (wrongCredentialsError.value) {
        WrongCredentialsErrorDialog(onDismissRequest = { wrongCredentialsError.value = false })
    }
}

////////////////////Sign-Up////////////////////////////////////
@Composable
fun SignUpScreen(authViewModel: AuthViewModel, navController: NavController) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val confirmPasswordState = remember { mutableStateOf("") }
    val signUpSuccessDialog = remember { mutableStateOf(false) }
    val signUpErrorDialog = remember { mutableStateOf(false) }
    val emptyFieldsError = remember { mutableStateOf(false) }
    val passwordMismatchError = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.dog_cat),
            contentDescription = "app logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "New User")
        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") }
        )
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        OutlinedTextField(
            value = confirmPasswordState.value,
            onValueChange = { confirmPasswordState.value = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (emailState.value.isEmpty() || passwordState.value.isEmpty() || confirmPasswordState.value.isEmpty()) {
                emptyFieldsError.value = true
            } else if (passwordState.value != confirmPasswordState.value) {
                passwordMismatchError.value = true
            } else {
                authViewModel.signUp(emailState.value, passwordState.value) { success ->
                    if (success) {
                        signUpSuccessDialog.value = true
                    } else {
                        signUpErrorDialog.value = true
                    }
                }
            }
        }) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // go back to login screen
        Button(onClick = {
            navController.navigate(Screen.Login.route)
        }) {
            Text("Back to Login")
        }
    }
    if (signUpSuccessDialog.value) {
        SignUpSuccessDialog(onDismissRequest = {
            signUpSuccessDialog.value = false
            navController.navigate(Screen.Login.route)
        })
    }
    if (signUpErrorDialog.value) {
        SignUpErrorDialog(onDismissRequest = { signUpErrorDialog.value = false })
    }
    if (emptyFieldsError.value) {
        EmptyFieldsErrorDialog(onDismissRequest = { emptyFieldsError.value = false })
    }
    if (passwordMismatchError.value) {
        PasswordMismatchErrorDialog(onDismissRequest = { passwordMismatchError.value = false })
    }
}


///////////////////All Alert Dialogs:///////////////////////////////////////
@Composable
fun LogoutConfirmationDialog(
    onLogoutConfirmed: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Logout") },
        text = { Text(text = "Are you sure you want to logout?") },
        confirmButton = {
            Button(
                onClick = {
                    onLogoutConfirmed()
                    onDismissRequest()
                }
            ) {
                Text(text = "Logout")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text(text = "Cancel")
            }
        }
    )
}
@Composable
fun SignUpSuccessDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Success") },
        text = { Text(text = "Sign up successful!") },
        confirmButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text(text = "OK")
            }
        }
    )
}


@Composable
fun PasswordMismatchErrorDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Error") },
        text = { Text(text = "Passwords do not match.") },
        confirmButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text(text = "OK")
            }
        }
    )
}

@Composable
fun WrongCredentialsErrorDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Error") },
        text = { Text(text = "Incorrect email or password.") },
        confirmButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text(text = "OK")
            }
        }
    )
}


@Composable
fun EmptyFieldsErrorDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Error") },
        text = { Text(text = "Please fill in all fields.") },
        confirmButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text(text = "OK")
            }
        }
    )
}

@Composable
fun SignUpErrorDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Check Email or Password") },
        text = { Text(text = "Password must be at least 6 characters long and" +
                " must use proper Email") },
        confirmButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text(text = "OK")
            }
        }
    )
}

/////////////////////////////////////////////////////////////////////////////////////////
package com.example.atividade

import com.example.atividade.MainActivity.Companion.productList
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.*
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    companion object {
        val productList = mutableListOf<Product>() // Lista estática de produtos
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavHostController()
        }
    }

    @Composable
    fun NavHostController() {
        val navController = rememberNavController()
        NavHost(navController, startDestination = "cadastro") {
            composable("cadastro") { CadastroProduto(navController) }
            composable("lista") { /* Tela de Lista de Produtos */ }
            composable("detalhes/{productId}") { /* Tela de Detalhes do Produto */ }
        }
    }

    data class Product(val name: String, val category: String, val price: Double, val quantity: Int)

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroProduto(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Cadastro de Produto")

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome do Produto") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Categoria") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Preço") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Quantidade em Estoque") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (name.isBlank() || category.isBlank() || price.isBlank() || quantity.isBlank()) {
                Toast.makeText(context, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show()
            } else {
                val product =
                    MainActivity.Product(name, category, price.toDouble(), quantity.toInt())
                productList.add(product)
                // Limpar os campos após o cadastro
                name = ""
                category = ""
                price = ""
                quantity = ""
                Toast.makeText(context, "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show()

                // Navegar para a lista de produtos (opcional)
                // navController.navigate("lista")
            }
        }) {
            Text("Cadastrar")
        }
    }
}

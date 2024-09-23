package com.example.atividade

import com.example.atividade.MainActivity.Companion.estoque
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
        val estoque = Estoque()
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
            composable("lista") { ListaProdutos(navController) }
            composable("detalhes/{produtoJson}") { backStackEntry ->
                val produtoJson = backStackEntry.arguments?.getString("produtoJson")
                DetalhesProduto(navController, produtoJson)
            }
            composable("estatisticas") { Estatisticas(navController) }
        }
    }

    data class Produto(val nome: String, val categoria: String, val preco: Double, val quantidade: Int)

    class Estoque {
        val listaProdutos = mutableListOf<Produto>()

        fun adicionarProduto(produto: Produto): Boolean {
            return if (produto.quantidade >= 1 && produto.preco >= 0) {
                listaProdutos.add(produto)
                true
            } else {
                false
            }
        }

        fun calcularValorTotalEstoque(): Double {
            return listaProdutos.sumOf { it.preco * it.quantidade }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroProduto(navController: NavController) {
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Cadastro de Produto")

        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do Produto") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoria") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = preco,
            onValueChange = { preco = it },
            label = { Text("Preço") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = quantidade,
            onValueChange = { quantidade = it },
            label = { Text("Quantidade em Estoque") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (nome.isBlank() || categoria.isBlank() || preco.isBlank() || quantidade.isBlank()) {
                Toast.makeText(context, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show()
            } else {
                val produto = MainActivity.Produto(nome, categoria, preco.toDouble(), quantidade.toInt())
                if (estoque.adicionarProduto(produto)) {
                    nome = ""
                    categoria = ""
                    preco = ""
                    quantidade = ""
                    Toast.makeText(context, "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                    navController.navigate("lista")
                } else {
                    Toast.makeText(context, "Preço deve ser maior ou igual a 0 e quantidade deve ser maior ou igual a 1", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Cadastrar")
        }
    }
}

@Composable
fun ListaProdutos(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Lista de Produtos")

        LazyColumn {
            items(estoque.listaProdutos.size) { index ->
                val produto = estoque.listaProdutos[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${produto.nome} (${produto.quantidade} unidades)",
                        modifier = Modifier.weight(1f)
                    )

                    Button(onClick = {
                        val produtoJson = Gson().toJson(produto)
                        navController.navigate("detalhes/$produtoJson")
                    }) {
                        Text("Detalhes")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("estatisticas") }) {
            Text("Estatísticas")
        }
    }
}

@Composable
fun DetalhesProduto(navController: NavController, produtoJson: String?) {
    val produto = Gson().fromJson(produtoJson, MainActivity.Produto::class.java)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Detalhes do Produto")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Nome: ${produto.nome}")
        Text("Categoria: ${produto.categoria}")
        Text("Preço: R$ ${produto.preco}")
        Text("Quantidade em Estoque: ${produto.quantidade} unidades")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}

@Composable
fun Estatisticas(navController: NavController) {
    val valorTotal = MainActivity.estoque.calcularValorTotalEstoque()
    val quantidadeTotal = MainActivity.estoque.listaProdutos.sumOf { it.quantidade }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Estatísticas do Estoque", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Valor Total do Estoque: R$ ${valorTotal.format(2)}")
        Text("Quantidade Total de Produtos: $quantidadeTotal")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

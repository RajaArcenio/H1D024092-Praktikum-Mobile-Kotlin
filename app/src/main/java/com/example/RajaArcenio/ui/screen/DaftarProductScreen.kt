package com.example.RajaArcenio.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.RajaArcenio.R
import com.example.RajaArcenio.data.model.Category
import com.example.RajaArcenio.data.model.Product
import com.example.RajaArcenio.ui.viewmodel.ProductUiState
import com.example.RajaArcenio.ui.viewmodel.ProductViewModel
import com.example.RajaArcenio.util.JualanConstants.BASE_URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarProdukScreen(navController: NavController? = null, viewModel: ProductViewModel) {
    var selectedCategoryId by rememberSaveable { mutableStateOf<Int?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is ProductUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ProductUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
            }
        }
        is ProductUiState.Success -> {
            if (selectedCategoryId == null && state.categories.isNotEmpty()) {
                selectedCategoryId = state.categories.first().id
            }

            val filteredByCategory = if (selectedCategoryId != null) {
                state.products.filter { it.category_id == selectedCategoryId }
            } else {
                state.products
            }

            val filteredProducts = if (searchQuery.isBlank()) {
                filteredByCategory
            } else {
                filteredByCategory.filter {
                    it.name.contains(other = searchQuery, ignoreCase = true)
                }
            }

            StatelessDaftarProduct(
                categories = state.categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = { selectedCategoryId = it },
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                products = filteredProducts,
                onProductClick = { product ->
                    navController?.navigate(route = "detail/${product.id}")
                },
                onContactUsClick = {
                    navController?.navigate(route = "hubungi_kami")
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatelessDaftarProduct(
    categories: List<Category>, selectedCategoryId: Int?, onCategorySelected: (Int) -> Unit,
    searchQuery: String, onSearchQueryChange: (String) -> Unit,
    products: List<Product>, onProductClick: (Product) -> Unit, onContactUsClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Produk UMKM") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { /* TODO: Navigasi Keranjang */ }) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Keranjang", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    IconButton(onClick = { expanded = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Hubungi Kami") },
                            onClick = {
                                expanded = false
                                onContactUsClick()
                            },
                            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email") }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Cari produk...") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )
            Text(text = "Kategori Produk", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(all = 16.dp))

            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    CategoryItem(category = category, isSelected = category.id == selectedCategoryId, onClick = { onCategorySelected(category.id) })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Daftar Produk", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Produk tidak ditemukan.")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(count = 2),
                    contentPadding = PaddingValues(all = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products) { product ->
                        ProductItemCard(product = product) {
                            onProductClick(product)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, isSelected: Boolean, onClick: () -> Unit) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Card(
        modifier = Modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor)
    ) {
        Text(text = category.name, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ProductItemCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(all = 8.dp).fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(all = 12.dp)) {
            val imageModel: Any = if (product.img == "dummy_product") {
                R.drawable.dummy_product
            } else {
                "${BASE_URL}img/${product.img}"
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(shape = RoundedCornerShape(size = 8.dp)).background(color = Color.White),
                    contentScale = ContentScale.Fit
                )
                if (product.category != null) {
                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(all = 4.dp).clip(shape = RoundedCornerShape(size = 4.dp)).background(color = MaterialTheme.colorScheme.secondary)) {
                        Text(text = product.category.name, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Rp ${product.price}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}
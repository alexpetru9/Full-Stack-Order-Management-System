import { Component, OnInit, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';

import { ProductService } from '../../services/product.services';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatSortModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    FormsModule,
    MatCardModule,
  ],
  templateUrl: './product-list.html',
  styleUrls: ['./product-list.scss'],
})
export class ProductListComponent implements OnInit {
  private readonly productService = inject(ProductService);

  dataSource = new MatTableDataSource<Product>([]);
  displayedColumns = ['name', 'price', 'actions'];

  @ViewChild(MatSort) sort!: MatSort;

  newName = '';
  newPrice: number | null = null;
  editingProductId: string | null = null;

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.productService.getProducts().subscribe((data) => {
      this.dataSource.data = data;
      this.dataSource.sort = this.sort;
    });
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  saveProduct(): void {
    if (this.newName && this.newPrice !== null) {
      if (this.editingProductId) {
        const updatedProduct: Product = {
          id: this.editingProductId,
          name: this.newName,
          price: this.newPrice,
        };
        this.productService.updateProduct(this.editingProductId, updatedProduct).subscribe(() => {
          this.loadProducts();
          this.cancelEdit();
        });
      } else {
        this.productService
          .addProduct({ name: this.newName, price: this.newPrice })
          .subscribe(() => {
            this.loadProducts();
            this.cancelEdit();
          });
      }
    }
  }

  editProduct(product: Product): void {
    this.editingProductId = product.id;
    this.newName = product.name;
    this.newPrice = product.price;
  }

  cancelEdit(): void {
    this.editingProductId = null;
    this.newName = '';
    this.newPrice = null;
  }

  deleteProduct(id: string): void {
    this.productService.deleteProduct(id).subscribe(() => {
      this.loadProducts();
    });
  }
}

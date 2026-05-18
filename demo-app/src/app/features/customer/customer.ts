import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

import { ProductService } from '../../services/product.services';
import { OrderService } from '../../services/order.services';
import { LoginStore } from '../login/login.store';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-customer',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './customer.html',
})
export class CustomerComponent implements OnInit {
  private readonly productService = inject(ProductService);
  private readonly orderService = inject(OrderService);
  public readonly loginStore = inject(LoginStore);

  products: Product[] = [];
  cart: Product[] = [];

  ngOnInit(): void {
    this.productService.getProducts().subscribe({
      next: (data: Product[]) => {
        this.products = data;
      },
      error: (err: unknown) => {
        console.error(err);
      },
    });
  }

  addToCart(product: Product): void {
    this.cart.push(product);
  }

  removeFromCart(index: number): void {
    this.cart.splice(index, 1);
  }

  placeOrder(): void {
    const userId = this.loginStore.id();
    const today = new Date().toISOString().split('T')[0];

    if (!userId) {
      alert('Error: You are not logged in properly.');
      return;
    }

    if (this.cart.length === 0) {
      alert('Error: Your cart is empty.');
      return;
    }

    this.orderService.addOrder({ orderDate: today, personId: userId }).subscribe({
      next: () => {
        alert('Order placed successfully!');
        this.cart = [];
      },
      error: (err: unknown) => {
        console.error(err);
        alert('An error occurred. Please try again.');
      },
    });
  }
}

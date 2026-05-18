import { Component, OnInit, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';

import { OrderService } from '../../services/order.services';
import { Order } from '../../models/order.model';

@Component({
  selector: 'app-order-list',
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
  templateUrl: './order-list.html',
  styleUrls: ['./order-list.scss'],
})
export class OrderListComponent implements OnInit {
  private readonly orderService = inject(OrderService);

  dataSource = new MatTableDataSource<Order>([]);
  displayedColumns = ['id', 'orderDate', 'actions'];

  @ViewChild(MatSort) sort!: MatSort;

  newOrderDate = '';
  newPersonId = '';
  editingOrderId: string | null = null;

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.orderService.getOrders().subscribe((data) => {
      this.dataSource.data = data;
      this.dataSource.sort = this.sort;
    });
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  saveOrder(): void {
    if (this.newOrderDate) {
      if (this.editingOrderId) {
        const updatedOrder: Order = { id: this.editingOrderId, orderDate: this.newOrderDate };
        this.orderService.updateOrder(this.editingOrderId, updatedOrder).subscribe(() => {
          this.loadOrders();
          this.cancelEdit();
        });
      } else {
        if (this.newPersonId) {
          this.orderService
            .addOrder({ orderDate: this.newOrderDate, personId: this.newPersonId })
            .subscribe(() => {
              this.loadOrders();
              this.cancelEdit();
            });
        }
      }
    }
  }

  editOrder(order: Order): void {
    this.editingOrderId = order.id;
    this.newOrderDate = order.orderDate;
    this.newPersonId = '';
  }

  cancelEdit(): void {
    this.editingOrderId = null;
    this.newOrderDate = '';
    this.newPersonId = '';
  }

  deleteOrder(id: string): void {
    this.orderService.deleteOrder(id).subscribe(() => {
      this.loadOrders();
    });
  }
}

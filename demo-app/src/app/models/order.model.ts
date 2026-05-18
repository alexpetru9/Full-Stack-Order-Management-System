export interface Order {
  id: string;
  orderDate: string;
}

export interface OrderCreateDTO {
  orderDate: string;
  personId: string;
}

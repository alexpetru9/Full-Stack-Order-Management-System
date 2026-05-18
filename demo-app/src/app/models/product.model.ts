export interface Product {
  id: string;
  name: string;
  price: number;
}

export interface ProductCreateDTO {
  name: string;
  price: number;
  categoryIds?: string[];
}

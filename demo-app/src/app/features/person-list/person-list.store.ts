import { computed, inject, Injectable, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { CreatePersonDto, Person, UpdatePersonDto } from '../../models/person.model';
import { PersonService } from '../../services/person.service';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class PersonListStore {
  private readonly personService = inject(PersonService);
  private readonly pendingRequests = signal(0);

  readonly persons = signal<Person[]>([]);
  readonly hasError = signal(false);
  readonly errorMessage = signal('');
  readonly isLoading = computed(() => this.pendingRequests() > 0);

  private beginRequest(): void {
    this.pendingRequests.update((count) => count + 1);
  }

  private endRequest(): void {
    this.pendingRequests.update((count) => Math.max(0, count - 1));
  }

  load(): void {
    this.hasError.set(false);
    this.beginRequest();
    this.personService
      .getAll()
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (data) => this.persons.set(data),
        error: () => this.hasError.set(true),
      });
  }

  create(dto: CreatePersonDto): void {
    this.hasError.set(false);
    this.beginRequest();
    this.personService
      .create(dto)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (created) => this.persons.update((list) => [...list, created]),
        error: (err: HttpErrorResponse) => {
          this.hasError.set(true);

          const errorObj = (err.error || {}) as Record<string, unknown>;

          let textReal = 'Is an error!';
          if (typeof errorObj['message'] === 'string') {
            textReal = errorObj['message'];
          } else {
            const firstString = Object.values(errorObj).find((val) => typeof val === 'string');
            if (typeof firstString === 'string') {
              textReal = firstString;
            }
          }

          this.errorMessage.set(textReal);
        },
      });
  }

  update(id: string, dto: UpdatePersonDto): void {
    const existing = this.persons().find((p) => p.id === id);
    if (!existing) return;

    const payload: CreatePersonDto = { ...dto, password: existing.password };

    this.hasError.set(false);
    this.beginRequest();
    this.personService
      .update(id, payload)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (updated) =>
          this.persons.update((list) =>
            list.map((person) => (person.id === updated.id ? updated : person)),
          ),
        error: (err: HttpErrorResponse) => {
          this.hasError.set(true);

          const errorObj = (err.error || {}) as Record<string, unknown>;

          let textReal = 'Is an error!';
          if (typeof errorObj['message'] === 'string') {
            textReal = errorObj['message'];
          } else {
            const firstString = Object.values(errorObj).find((val) => typeof val === 'string');
            if (typeof firstString === 'string') {
              textReal = firstString;
            }
          }

          this.errorMessage.set(textReal);
        },
      });
  }

  remove(id: string): void {
    this.hasError.set(false);
    this.beginRequest();
    this.personService
      .delete(id)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: () => this.persons.update((list) => list.filter((person) => person.id !== id)),
        error: () => this.hasError.set(true),
      });
  }
}

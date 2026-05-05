import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListaElementos } from './lista-elementos';

describe('ListaElementos', () => {
  let component: ListaElementos;
  let fixture: ComponentFixture<ListaElementos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListaElementos],
    }).compileComponents();

    fixture = TestBed.createComponent(ListaElementos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

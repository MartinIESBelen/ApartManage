import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListaContratos } from './lista-contratos';

describe('ListaContratos', () => {
  let component: ListaContratos;
  let fixture: ComponentFixture<ListaContratos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListaContratos],
    }).compileComponents();

    fixture = TestBed.createComponent(ListaContratos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

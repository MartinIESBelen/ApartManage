import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApartamentoEdit } from './apartamento-edit';

describe('ApartamentoEdit', () => {
  let component: ApartamentoEdit;
  let fixture: ComponentFixture<ApartamentoEdit>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApartamentoEdit],
    }).compileComponents();

    fixture = TestBed.createComponent(ApartamentoEdit);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

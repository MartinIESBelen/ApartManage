import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApartamentoCreate } from './apartamento-create';

describe('ApartamentoCreate', () => {
  let component: ApartamentoCreate;
  let fixture: ComponentFixture<ApartamentoCreate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApartamentoCreate],
    }).compileComponents();

    fixture = TestBed.createComponent(ApartamentoCreate);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

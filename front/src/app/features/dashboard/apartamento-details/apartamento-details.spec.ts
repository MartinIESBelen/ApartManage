import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApartamentoDetails } from './apartamento-details';

describe('ApartamentoDetails', () => {
  let component: ApartamentoDetails;
  let fixture: ComponentFixture<ApartamentoDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApartamentoDetails],
    }).compileComponents();

    fixture = TestBed.createComponent(ApartamentoDetails);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

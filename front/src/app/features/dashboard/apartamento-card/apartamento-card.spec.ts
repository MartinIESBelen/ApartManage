import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApartamentoCard } from './apartamento-card';

describe('ApartamentoCard', () => {
  let component: ApartamentoCard;
  let fixture: ComponentFixture<ApartamentoCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApartamentoCard],
    }).compileComponents();

    fixture = TestBed.createComponent(ApartamentoCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

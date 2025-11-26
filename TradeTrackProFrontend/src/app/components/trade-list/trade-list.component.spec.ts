import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TradeListComponent } from './trade-list.component';

describe('TradeListComponent', () => {
  let component: TradeListComponent;
  let fixture: ComponentFixture<TradeListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TradeListComponent]
    });
    fixture = TestBed.createComponent(TradeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

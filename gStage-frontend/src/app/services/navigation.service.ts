// navigation.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NavigationService {
  private activeTabSource = new BehaviorSubject<string>('dashboard');
  activeTab$ = this.activeTabSource.asObservable();

  setActiveTab(tab: string) {
    this.activeTabSource.next(tab);
  }
}
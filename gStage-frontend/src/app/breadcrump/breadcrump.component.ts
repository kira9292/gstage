import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router, RouterLink } from '@angular/router';
import { filter } from 'rxjs';

interface BreadcrumbItem {
  label: string;
  path: string;
  icon: string;
}

@Component({
  selector: 'app-breadcrump',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './breadcrump.component.html',
  styleUrl: './breadcrump.component.scss'
})


export class BreadcrumbComponent implements OnInit {
  breadcrumbs: BreadcrumbItem[] = [];

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit() {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.breadcrumbs = this.createBreadcrumbs(this.activatedRoute.root);
      });
  }

  private createBreadcrumbs(
    route: ActivatedRoute, 
    url: string = '', 
    breadcrumbs: BreadcrumbItem[] = []
  ): BreadcrumbItem[] {
    const children: ActivatedRoute[] = route.children;

    if (children.length === 0) {
      return breadcrumbs;
    }

    for (const child of children) {
      const routeURL: string = child.snapshot.url.map(segment => segment.path).join('/');
      
      if (routeURL !== '') {
        // Gérer les segments d'URL séparément
        const segments = routeURL.split('/');
        let currentPath = url;
        
        for (const segment of segments) {
          currentPath += `/${segment}`;
          const label = this.getRouteLabel(segment);
          
          if (label) {
            // Vérifier si ce segment n'existe pas déjà dans les breadcrumbs
            const exists = breadcrumbs.some(b => b.path === currentPath);
            if (!exists) {
              breadcrumbs.push({
                label: label,
                path: currentPath,
                icon: this.getRouteIcon(segment)
              });
            }
          }
        }
      }

      return this.createBreadcrumbs(child, url + `/${routeURL}`, breadcrumbs);
    }

    return breadcrumbs;
  }

  private getRouteLabel(path: string): string {
    const routeLabels: { [key: string]: string } = {
      'dashboard': 'Tableau de Bord',
      'application': 'Renouvellement',
      'documents': 'Mes Documents',
      'contracts': 'Mes Contrats',
      'contrats': 'Mes Contrats',  // Ajout de la version française
      'attestations': 'Attestations'  // Nouveau label pour attestations
    };
    return routeLabels[path] || path;
  }

  private getRouteIcon(path: string): string {
    const routeIcons: { [key: string]: string } = {
      'dashboard': 'fa-chart-line',
      'application': 'fa-file-alt',
      'documents': 'fa-folder',
      'contracts': 'fa-file-contract',
      'contrats': 'fa-file-contract',  // Ajout de la version française
      'attestations': 'fa-certificate'  // Icône pour attestations
    };
    return routeIcons[path] || 'fa-circle';
  }
  
}
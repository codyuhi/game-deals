export interface StoreImages {
  banner: string;
  logo: string;
  icon: string;
}

export interface Store {
  storeID: string;
  storeName: string;
  isActive: number;
  images: StoreImages;
}

export interface Deal {
  title: string;
  dealID: string;
  storeID: string;
  gameID: string;
  salePrice: string;
  normalPrice: string;
  savings: string;
  dealRating: string;
  releaseDate: number;
  steamRatingText: string | null;
  steamRatingPercent: string;
  steamRatingCount: string;
  metacriticScore: string;
  metacriticLink?: string;
  thumb: string;
}

// Model returned by `/deals?id=...` API
export interface DealDetailsData {
  gameInfo: {
    storeID: string;
    gameID: string;
    name: string;
    steamAppID: string | null;
    salePrice: string;
    retailPrice: string;
    steamRatingText: string | null;
    steamRatingPercent: string;
    steamRatingCount: string;
    metacriticScore: string;
    metacriticLink: string;
    releaseDate: number;
    publisher: string;
    steamWorks: string | null;
    thumb: string;
  };
  cheapestPrice: {
    price: string;
    date: number;
  } | null;
  cheaperStores: string[];
}

export interface GameDeal {
  storeID: string;
  dealID: string;
  price: string;
  retailPrice: string;
  savings: string;
}

// Model returned by `/games?id=...` API
export interface GameDetailsData {
  info: {
    title: string;
    steamAppID: string | null;
    thumb: string;
  };
  cheapestPriceEver: {
    price: string;
    date: number;
  };
  deals: GameDeal[];
}

// Model returned by `/games?title=...` search API
export interface SearchResult {
  gameID: string;
  steamAppID: string | null;
  cheapest: string;
  cheapestDealID: string;
  external: string;
  thumb: string;
}

export interface FavoriteItem {
  name: string;
  id: string; // gameID
}

export interface FavoriteGroup {
  nickname: string;
  favoriteList: FavoriteItem[];
}

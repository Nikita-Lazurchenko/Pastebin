package pet.project.service;

import org.springframework.stereotype.Service;

@Service
public class UserRatingService {

    public double calculateRating(long totalViewPastes,
                                  double averageUserViews,
                                  double averageViewAllPastes,
                                  int daysAfterPublications) {

        double qualityWeight = getQualityWeight(averageUserViews, averageViewAllPastes);
        double actionWeight = getActionWeight(daysAfterPublications);

        double rawRating = Math.log(totalViewPastes + 1) * qualityWeight * actionWeight;

        return Math.min(5, rawRating);
    }

    private double getQualityWeight(double averageUserViews, double averageViewsAllPastes) {
        if (averageViewsAllPastes <= 0) return 1.0;
        return (averageUserViews / averageViewsAllPastes) + 1;
    }

    private double getActionWeight(int daysAfterPublications) {
        return Math.max(0.5, 1 - ((double) daysAfterPublications / 30.0 * 0.1));
    }
}